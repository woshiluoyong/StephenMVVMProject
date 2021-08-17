package com.stephen.test.mvvm.framework.fragments

import android.os.Bundle
import com.android.puy.mvvm.base.XFragmentation
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.databinding.FragmentPageOneBinding
import com.stephen.test.mvvm.framework.fragments.viewmodel.PageOneViewModel

class PageOne :XFragmentation<PageOneViewModel, FragmentPageOneBinding>() {

    override fun getLayoutId(): Int  = R.layout.fragment_page_one

    override fun initViewMode() = getViewModel(PageOneViewModel::class.java)

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initData() {}
}