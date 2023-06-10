package com.nymousdevapps.barcodescannerapp.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.R
import com.nymousdevapps.barcodescannerapp.Utils.BarcodeItem
import com.nymousdevapps.barcodescannerapp.Utils.CustomProgressDialog
import com.nymousdevapps.barcodescannerapp.adapters.ProductScannerAdapter
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.databinding.FragmentProductCartBinding
import com.nymousdevapps.barcodescannerapp.ui.ContinuousCaptureActivity
import com.nymousdevapps.barcodescannerapp.ui.products.viewModel.ProductCartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCartFragment : Fragment(), ProductScannerAdapter.IProductScannerListener {
    private var _binding: FragmentProductCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductCartViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }


    private var activityResultLaunch = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bundle = result.data?.getBundleExtra("bundle")
            val resultList = bundle?.getSerializable("scannerResultList") as ArrayList<BarcodeItem>
            setScannedData(resultList)
        } else {
            val bundle = result.data?.getBundleExtra("bundle")
            val resultList = bundle?.getSerializable("scannerResultList") as ArrayList<BarcodeItem>
            setScannedData(resultList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        barcodeMenu()

        binding.btnRegister.setOnClickListener {
            val text = BarcodeScannerAppApplication.resource?.getString(R.string.msg_saving_data)
            progressDialog.start(text!!)
            viewModel.productsListScanned.value?.let { it1 -> viewModel.updateProductQuantities(it1) }
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            progressDialog.stop()
            binding.txtError.isVisible = true
            binding.txtError.text = errorMessage
        }
        val resultObserver = Observer<Boolean> { value ->
            if (value) {
                binding.txtError.isVisible = false
                binding.edtName.setText("")
                viewModel.clearList()
                progressDialog.stop()
            }
        }

        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.updateProduct.observe(this, resultObserver)
    }

    private fun setRecycler() {
        binding.rvProductsScanned.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsScanned.adapter = ProductScannerAdapter(mutableListOf(), this)
    }

    private fun setScannedData(data: ArrayList<BarcodeItem>) {
        viewModel.getProductByBarcode(data)
        viewModel.productsListScanned.observe(viewLifecycleOwner) {
            (binding.rvProductsScanned.adapter as ProductScannerAdapter).updateList(it.toMutableList())
        }
    }

    override fun onProductTap(product: Product) {

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
                                val intent = Intent(activity, ContinuousCaptureActivity::class.java)
                                intent.putExtra("typeFragmentScanner", 3)
                                activityResultLaunch.launch(intent)
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
}