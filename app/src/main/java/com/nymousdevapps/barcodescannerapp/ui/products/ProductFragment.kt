package com.nymousdevapps.barcodescannerapp.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.R
import com.nymousdevapps.barcodescannerapp.Utils.CustomProgressDialog
import com.nymousdevapps.barcodescannerapp.databinding.FragmentProductBinding
import com.nymousdevapps.barcodescannerapp.ui.products.viewModel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            val text = BarcodeScannerAppApplication.resource?.getString(R.string.msg_saving_data)
            progressDialog.start(text!!)
            val name = binding.edtName.text.toString()
            val code = binding.edtCode.text.toString()
            val description = binding.edtDescription.text.toString()
            val barcode = binding.edtBarcode.text.toString()
            viewModel.registerProduct(name, code, description, barcode)
        }
        barcodeMenu()
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.txtError.isVisible = true
            binding.txtError.text = errorMessage
        }
        val resultObserver = Observer<Boolean> { value ->
            if (value) {
                binding.txtError.isVisible = false
                binding.edtName.setText("")
                binding.edtCode.setText("")
                binding.edtName.setText("")
                binding.edtBarcode.setText("")
                progressDialog.stop()
                findNavController().navigateUp()
            }
        }

        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.registerProduct.observe(this, resultObserver)
    }

    private fun barcodeMenu() {
        binding.topAppBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                requireActivity().menuInflater
                item?.let {
                    if (item.itemId == R.id.filter_options) {
                        val itemView = binding.root.findViewById<View>(R.id.filter_options)
                        val popupMenu = PopupMenu(requireContext(), itemView)
                        popupMenu.menuInflater.inflate(R.menu.scanner_menu, popupMenu.menu)
                        popupMenu.show()
                        popupMenu.setOnMenuItemClickListener { item ->
                            item?.let {

                            }
                            false
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}