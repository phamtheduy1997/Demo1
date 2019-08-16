package com.rikkei.pets.adapter


//import android.support.v4.app.FragmentManager
//import android.support.v4.app.FragmentPagerAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
//import android.support.v4.app.Fragment as Fragment

@Suppress("DEPRECATION")
class PetsAdapter (manager: FragmentManager):FragmentPagerAdapter(manager){
    val fragments = ArrayList<Fragment>()
    val titles = ArrayList<String>()
    override fun getItem(position: Int): Fragment = fragments.get(position)

    override fun getCount(): Int = fragments.size
    override fun getPageTitle(position: Int): CharSequence? = titles.get(position)
    fun addFragment(fragment: Fragment,title: String){
        fragments.add(fragment)
        titles.add(title)

    }

}