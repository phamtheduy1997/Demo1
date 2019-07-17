package com.t3h.demo1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DialogTitle
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_pets.*
import android.widget.Adapter as Adapter

class PetsFragment : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pets)
        setSupportActionBar(toolbar)
        setupViewPager(pager)
        tabs.setupWithViewPager(pager)
    }

    private fun setupViewPager(pager: ViewPager?) {
        val adapter = Adapter(supportFragmentManager)
        val f1 = Pets.newInstance("Dogs")
        adapter.addFragmnet(f1,"Dogs")
        val f2 = Pets.newInstance("Cats")
        adapter.addFragmnet(f1,"Cats")
        val f3 = Pets.newInstance("Birds")
        adapter.addFragmnet(f1,"Birds")
        pager?.adapter = adapter

    }

    private class Adapter(manager: FragmentManager): FragmentPagerAdapter(manager){
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        override fun getItem(position: Int): Fragment = fragments.get(position)

        override fun getCount(): Int = fragments.size
        override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
        fun addFragmnet(fragment: Fragment,title: String){
            fragments.add(fragment)
            titles.add(title)
        }




    }
}