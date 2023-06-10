package com.nymousdevapps.barcodescannerapp.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.R
import com.nymousdevapps.barcodescannerapp.Utils.CustomProgressDialog
import com.nymousdevapps.barcodescannerapp.adapters.ProductAdapter
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.databinding.FragmentProductListBinding
import com.nymousdevapps.barcodescannerapp.ui.products.viewModel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment(), ProductAdapter.IProductListener {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = BarcodeScannerAppApplication.resource?.getString(R.string.msg_saving_data)
        //progressDialog.start(text!!)
        setRecycler()

        viewModel.productsList.observe(viewLifecycleOwner) {
            (binding.rvProducts.adapter as ProductAdapter).updateList(it.toMutableList())
            if (it.isEmpty()) {
                progressDialog.stop()
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                progressDialog.stop()
                binding.tvNoData.visibility = View.GONE
            }
        }

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.productFragment)
        }

        cartMenu()
        viewModel.getProducts()
    }

    private fun setRecycler() {
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = ProductAdapter(mutableListOf(), this)
    }

    override fun onProductTap(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable("productDetail", product)
        findNavController().navigate(R.id.productDetailFragment, bundle)
    }

    private fun cartMenu() {
        binding.topAppBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                requireActivity().menuInflater
                item?.let {
                    if (item.itemId == R.id.shopping_cart_option) {
                        val itemView = binding.root.findViewById<View>(R.id.shopping_cart_option)
                        val popupMenu = PopupMenu(requireContext(), itemView)
                        popupMenu.menuInflater.inflate(R.menu.cart_menu, popupMenu.menu)
                        popupMenu.show()
                        popupMenu.setOnMenuItemClickListener { item ->
                            item?.let {
                                findNavController().navigate(R.id.productCartFragment)
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