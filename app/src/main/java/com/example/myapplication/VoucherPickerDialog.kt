package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.VoucherModalAdapter
import com.example.myapplication.databinding.DialogVoucherPickerBinding
import com.example.myapplication.model.Voucher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.Toast

class VoucherPickerDialog(
    private val vouchers: List<Voucher>,
    private val onVoucherSelected: (Voucher?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogVoucherPickerBinding? = null
    private val binding get() = _binding!!
    private var selectedVoucher: Voucher? = null
    private var selectedPosition = -1
    private lateinit var adapter: VoucherModalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogVoucherPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        //updateApplyButton()
    }

    private fun setupRecyclerView() {
        // ✅ Buat adapter dengan selectedPosition yang benar
        adapter = VoucherModalAdapter(
            vouchers = vouchers,
            selectedPosition = selectedPosition,
            onItemClick = { voucher, position ->
                // Toggle selection
                selectedVoucher = if (selectedPosition == position) {
                    null
                } else {
                    voucher
                }
                selectedPosition = if (selectedPosition == position) -1 else position

                // Toast.makeText(requireContext(),"✅ ${selectedPosition} dipilih",Toast.LENGTH_SHORT).show()
                updateApplyButton()
                // ✅ Update adapter dengan posisi baru
                adapter.updateSelectedPosition(selectedPosition)
                adapter.notifyDataSetChanged()
            }
        )
        binding.rvVoucherList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVoucherList.adapter = adapter
    }

    private fun setupListeners() {
        // ✅ Tombol Terapkan - satu-satunya yang memanggil callback
        binding.btnApply.setOnClickListener {
            if (selectedVoucher != null) {
                // Kirim voucher yang dipilih ke CheckoutActivity
                onVoucherSelected(selectedVoucher)
                Toast.makeText(
                    requireContext(),
                    "✅ Voucher ${selectedVoucher!!.code} diterapkan!",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Pilih voucher terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ✅ Tombol Batal - kirim null, TAPI tanpa Toast agar tidak double
        binding.btnCancel.setOnClickListener {
            // Kirim null ke CheckoutActivity (berarti batal pilih voucher)
            onVoucherSelected(null)
            dismiss()
        }
    }

     // ✅ Fungsi untuk update tombol Terapkan
    private fun updateApplyButton() {
        if (selectedVoucher != null) {
            binding.btnApply.isEnabled = true
            binding.btnApply.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#1A73E8")
            )
            binding.btnApply.text = "✅ Terapkan ${selectedVoucher!!.code}"
        } else {
            binding.btnApply.isEnabled = false
            binding.btnApply.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#BDBDBD")
            )
            binding.btnApply.text = "✅ Pilih Voucher"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(vouchers: List<Voucher>, onVoucherSelected: (Voucher?) -> Unit): VoucherPickerDialog {
            return VoucherPickerDialog(vouchers, onVoucherSelected)
        }
    }
}