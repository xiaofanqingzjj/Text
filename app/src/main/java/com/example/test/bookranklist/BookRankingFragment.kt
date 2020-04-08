package com.example.test.bookranklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_book_ranking.*


class BookRankingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction().add(R.id.fl_contont_container, BookRankingListFragment()).commitAllowingStateLoss()

        csv.setData(mutableListOf<Channel>().apply {
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))
            add(Channel(name ="频道1"))


        })
    }

}