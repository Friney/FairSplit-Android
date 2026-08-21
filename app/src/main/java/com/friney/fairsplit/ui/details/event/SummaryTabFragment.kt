package com.friney.fairsplit.ui.details.event

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentSummaryTabBinding
import com.friney.fairsplit.network.model.summary.Debt
import com.friney.fairsplit.network.model.summary.User
import com.friney.fairsplit.ui.adapter.DebtsAdapter
import com.friney.fairsplit.ui.adapter.ReceiptsSummaryAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.util.Locale

@AndroidEntryPoint
class SummaryTabFragment : Fragment() {

    private var _binding: FragmentSummaryTabBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: DetailsEventViewModel by activityViewModels()
    lateinit var debtsAdapter: DebtsAdapter
    lateinit var receiptsSummaryAdapter: ReceiptsSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryTabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeSummary()
        mBinding.btnSavePdf.setOnClickListener {
            saveFragmentAsPdf()
        }
    }

    private fun initAdapter() {
        debtsAdapter = DebtsAdapter()
        receiptsSummaryAdapter = ReceiptsSummaryAdapter()

        mBinding.debtsRecyclerView.apply {
            adapter = debtsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        mBinding.receiptsRecyclerView.apply {
            adapter = receiptsSummaryAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun observeSummary() {
        viewModel.summaryLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let { summary ->
                        mBinding.totalAmount.text =
                            String.format(Locale.getDefault(), "%.2f", summary.total)

                        val optimized = optimizeDebts(summary.debts)
                        val sortedOptimized = optimized.sortedWith(
                            compareBy(
                                { it.from.displayName.ifBlank { it.from.name } },
                                { it.to.displayName.ifBlank { it.to.name } }
                            ))

                        debtsAdapter.differ.submitList(sortedOptimized)

                        receiptsSummaryAdapter.differ.submitList(summary.receipts)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get summary: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun optimizeDebts(debts: List<Debt>): List<Debt> {
        val optimized = mutableMapOf<Pair<User, User>, BigDecimal>()

        debts.forEach { debt ->
            optimized[debt.from to debt.to] = debt.amount
        }

        debts.forEach { debt ->
            val key = debt.from to debt.to
            val reverseKey = debt.to to debt.from

            if (optimized.containsKey(key) && optimized.containsKey(reverseKey)) {
                val a = optimized[key]!!
                val b = optimized[reverseKey]!!
                val diff = a - b

                when {
                    diff > BigDecimal.ZERO -> {
                        optimized[key] = diff
                        optimized.remove(reverseKey)
                    }

                    diff < BigDecimal.ZERO -> {
                        optimized[reverseKey] = diff.abs()
                        optimized.remove(key)
                    }

                    else -> {
                        optimized.remove(key)
                        optimized.remove(reverseKey)
                    }
                }
            }
        }

        return optimized.map { (fromAndTo, amount) ->
            Debt(
                amount,
                fromAndTo.first,
                fromAndTo.second
            )
        }
    }

    private fun saveFragmentAsPdf() {
        val context = requireContext()
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1
        var y = 50

        fun newPage(): Pair<PdfDocument.Page, Canvas> {
            val pageInfo =
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            val page = document.startPage(pageInfo)
            return page to page.canvas
        }

        var (page, canvas) = newPage()

        val paintTitle = android.graphics.Paint().apply { textSize = 24f; isFakeBoldText = true }
        val paintHeader = android.graphics.Paint().apply { textSize = 18f; isFakeBoldText = true }
        val paintText = android.graphics.Paint().apply { textSize = 14f }

        fun checkPageBreak(lines: Int = 1) {
            if (y + lines * 20 > pageHeight - 40) {
                document.finishPage(page)
                y = 50
                val result = newPage()
                page = result.first
                canvas = result.second
            }
        }

        // Заголовок
        canvas.drawText("Сводка по событию", 40f, y.toFloat(), paintTitle)
        y += 40

        // Общая сумма
        canvas.drawText(
            "Общая сумма: ${mBinding.totalAmount.text} ₽",
            40f,
            y.toFloat(),
            paintHeader
        )
        y += 30

        // Долги
        canvas.drawText("Кто кому должен:", 40f, y.toFloat(), paintHeader)
        y += 25
        val debts = debtsAdapter.differ.currentList
        if (debts.isEmpty()) {
            canvas.drawText("Нет долгов", 60f, y.toFloat(), paintText)
            y += 20
        } else {
            debts.forEach { debt ->
                checkPageBreak()
                val from = debt.from.displayName.ifBlank { debt.from.name }
                val to = debt.to.displayName.ifBlank { debt.to.name }
                canvas.drawText(
                    "$from → $to: ${debt.amount.setScale(2)} ₽",
                    60f,
                    y.toFloat(),
                    paintText
                )
                y += 20
            }
        }
        y += 20

        // Чеки
        canvas.drawText("Чеки:", 40f, y.toFloat(), paintHeader)
        y += 25
        val receipts = receiptsSummaryAdapter.differ.currentList
        if (receipts.isEmpty()) {
            canvas.drawText("Нет чеков", 60f, y.toFloat(), paintText)
            y += 20
        } else {
            receipts.forEach { receipt ->
                checkPageBreak()
                // Собираем имена плательщиков по чеку
                val payerNames =
                    receipt.payerInfos.map { it.user.displayName.ifBlank { it.user.name } }
                        .distinct().joinToString(", ")
                val receiptTitle = "${receipt.name} — ${receipt.total.setScale(2)} ₽"
                canvas.drawText(receiptTitle, 60f, y.toFloat(), paintText)
                y += 18
                // Покупки по чеку
                receipt.expenses.forEach { expense ->
                    checkPageBreak()
                    canvas.drawText(
                        "  • ${expense.name}: ${expense.total.setScale(2)} ₽",
                        80f,
                        y.toFloat(),
                        paintText
                    )
                    y += 16
                    // Участники покупки
                    expense.payerInfos.forEach { payer ->
                        checkPageBreak()
                        val payerName = payer.user.displayName.ifBlank { payer.user.name }
                        canvas.drawText(
                            "      - $payerName: ${payer.total.setScale(2)} ₽",
                            120f,
                            y.toFloat(),
                            paintText
                        )
                        y += 16
                    }
                }
                y += 8
            }
        }
        // --- Информация по людям ---
        y += 20
        checkPageBreak(2)
        canvas.drawText("Информация по людям:", 40f, y.toFloat(), paintHeader)
        y += 25
        // Собираем все уникальные имена пользователей
        val userMap =
            mutableMapOf<String, MutableList<Triple<String, String, java.math.BigDecimal>>>() // имя -> список (чек, позиция, сумма)
        receipts.forEach { receipt ->
            receipt.expenses.forEach { expense ->
                expense.payerInfos.forEach { payer ->
                    val userName = payer.user.displayName.ifBlank { payer.user.name }
                    val list = userMap.getOrPut(userName) { mutableListOf() }
                    list.add(Triple(receipt.name, expense.name, payer.total))
                }
            }
        }
        userMap.forEach { (user, items) ->
            checkPageBreak()
            canvas.drawText(user + ":", 60f, y.toFloat(), paintText)
            y += 18
            items.forEach { (receiptName, expenseName, amount) ->
                checkPageBreak()
                canvas.drawText(
                    "  $receiptName — $expenseName: ${amount.setScale(2)} ₽",
                    80f,
                    y.toFloat(),
                    paintText
                )
                y += 16
            }
            y += 8
        }
        document.finishPage(page)
        val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
        val fileName = "FairSplit_Summary_${sdf.format(java.util.Date())}.pdf"
        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
        if (dir != null && !dir.exists()) dir.mkdirs()
        val file = java.io.File(dir, fileName)
        try {
            val out = java.io.FileOutputStream(file)
            document.writeTo(out)
            out.close()
            android.widget.Toast.makeText(
                context,
                "PDF сохранён: ${file.absolutePath}",
                android.widget.Toast.LENGTH_LONG
            ).show()
            val pdfUri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
            val openIntent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, pdfUri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser =
                android.content.Intent.createChooser(sendIntent, "Открыть или отправить PDF")
            chooser.putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, arrayOf(openIntent))
            context.startActivity(chooser)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                "Ошибка сохранения PDF: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
        document.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

