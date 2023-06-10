package com.nymousdevapps.barcodescannerapp.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.R
import com.nymousdevapps.barcodescannerapp.Utils.CustomProgressDialog
import com.nymousdevapps.barcodescannerapp.Utils.toEditable
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.databinding.FragmentProductDetailBinding
import com.nymousdevapps.barcodescannerapp.ui.ContinuousCaptureActivity
import com.nymousdevapps.barcodescannerapp.ui.products.viewModel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private var product: Product? = null
    private val viewModel: ProductDetailViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }
    private var countItemsPref = 0
    private var countItems = 0

    private var activityResultLaunch = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (countItems != 0) {
                val newScanned = result.data?.getIntExtra("quantity", 0) ?: 0
                countItems += newScanned
                getScannedData(countItems.toString())
            } else {
                countItems = result.data?.getIntExtra("quantity", 0) ?: 0
                getScannedData(countItems.toString())
            }
        } else {
            if (countItems != 0) {
                val newScanned = BarcodeScannerAppApplication.sharedPref?.getInt("QuantityDetail", 0) ?: 0
                countItemsPref += newScanned
                getScannedData(countItemsPref.toString())
            } else {
                countItemsPref = BarcodeScannerAppApplication.sharedPref?.getInt("QuantityDetail", 0) ?: 0
                getScannedData(countItemsPref.toString())
            }
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
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product = arguments?.getSerializable("productDetail") as Product

        if (product != null) {
            binding.txtName.text = "Nombre: ${product?.name}"
            binding.txDescription.text = "Descripcion: ${product?.description}"
            binding.txtBarcode.text = "Codigo De Barras: ${product?.barcode}"
        }

        binding.btnOpenScanner.setOnClickListener {
            val intent = Intent(activity, ContinuousCaptureActivity::class.java)
            intent.putExtra("typeFragmentScanner", 2)
            intent.putExtra("barcode",product?.barcode)
            activityResultLaunch.launch(intent)
        }

        binding.btnSaveItemScanned.setOnClickListener {
            val text = BarcodeScannerAppApplication.resource?.getString(R.string.msg_saving_data)
            progressDialog.start(text!!)
            viewModel.updateProduct(product!!, countItems)
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
                binding.edtQuantity.setText("")
                progressDialog.stop()
                findNavController().navigateUp()
            }
        }

        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.updateProduct.observe(this, resultObserver)
    }

    private fun getScannedData(data: String) {
        binding.edtQuantity.text = data.toEditable()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLaunch.unregister()
    }
}