package com.example.capstonedesign.view.board

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.capstonedesign.R
import com.example.capstonedesign.adapter.BoardPostAdapter
import com.example.capstonedesign.databinding.FragmentRequestBoardBinding
import com.example.capstonedesign.model.board.ContentList
import com.example.capstonedesign.repository.BoardRepository
import com.example.capstonedesign.util.Constants.LOGIN_STATUS
import com.example.capstonedesign.util.SeggeredGridSpaceItemDecoration
import com.example.capstonedesign.viewmodel.BoardViewModel
import com.example.capstonedesign.viewmodel.factory.BoardViewModelFactory
import kotlinx.android.synthetic.main.dialog_login.*
import java.lang.Exception

class RequestBoardFragment: Fragment() {
    private var _binding: FragmentRequestBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestBoardPostAdapter: BoardPostAdapter
    private lateinit var viewModel: BoardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRequestBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataSettings()
        setObserver()
    }

    private fun setObserver() {
        viewModel.PostListResponse.observe(viewLifecycleOwner) {
            setRvPost(it.content)
            binding.pbRequestBoard.visibility = View.GONE
        }
    }

    private fun initDataSettings() {
        val repository = BoardRepository()
        val factory = BoardViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BoardViewModel::class.java]

        try {
            viewModel.getAllPost("QUESTION")
        } catch (e: Exception) {
            binding.tvBoardError.visibility = View.VISIBLE
        }
    }

    private fun setRvPost(requestBoardList: List<ContentList>) {
        requestBoardPostAdapter = BoardPostAdapter(requireContext(), requestBoardList)

        binding.rvRequestPost.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = requestBoardPostAdapter
            addItemDecoration(SeggeredGridSpaceItemDecoration(requireContext(),2))
        }

        requestBoardPostAdapter.setItemClickListener(object : BoardPostAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                if (LOGIN_STATUS) {
                    val action = BoardFragmentDirections.actionFragmentBoardToFragmentPostDetail(requestBoardList[position].boardId)
                    findNavController().navigate(action)
                } else {
                    setLoginDialog()
                }

            }
        })
    }

    private fun setLoginDialog() {
        val loginDialog = Dialog(requireContext())

        loginDialog.setContentView(R.layout.dialog_login)
        loginDialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        loginDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loginDialog.setCanceledOnTouchOutside(false)
        loginDialog.show()

        loginDialog.btn_dialog_login.setOnClickListener {
            loginDialog.dismiss()

            val action = BoardFragmentDirections.actionFragmentBoardToFragmentLogin()
            findNavController().navigate(action)
        }

        loginDialog.btn_dialog_login_close.setOnClickListener {
            loginDialog.dismiss()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}