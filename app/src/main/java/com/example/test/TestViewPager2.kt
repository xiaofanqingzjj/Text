package com.example.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.text.BidiFormatter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.recycleradapter.QuickAdapter
import kotlinx.android.synthetic.main.fragment_viewpager2.*


class TestViewPager2 : SimpleFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_viewpager2)
    }

    val list = mutableListOf<String>().apply {
        repeat(10) {
            add("$it")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager2.adapter = QuickAdapter<String>(context!!, list, R.layout.item_card_layout) {position, data, itemView ->
            itemView?.findViewById<TextView>(R.id.label_center)?.text = data
        }
    }


    class CardViewAdapter : RecyclerView.Adapter<CardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            return CardViewHolder(CardView(LayoutInflater.from(parent.context), parent))
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.bind(Card.DECK[position])
        }

        override fun getItemCount(): Int {
            return Card.DECK.size
        }
    }

    class CardViewHolder internal constructor(private val cardView: CardView) :
            RecyclerView.ViewHolder(cardView.view) {
        internal fun bind(card: Card) {
            cardView.bind(card)
        }
    }
}


/**
 * Playing card
 */
class Card private constructor(val suit: String, val value: String) {

    val cornerLabel: String
        get() = value + "\n" + suit

    /** Use in conjunction with [Card.fromBundle]  */
    fun toBundle(): Bundle {
        val args = Bundle(1)
        args.putStringArray(ARGS_BUNDLE, arrayOf(suit, value))
        return args
    }

    override fun toString(): String {
        val bidi = BidiFormatter.getInstance()
        if (!bidi.isRtlContext) {
            return bidi.unicodeWrap("$value $suit")
        } else {
            return bidi.unicodeWrap("$suit $value")
        }
    }

    companion object {
        internal val ARGS_BUNDLE = Card::class.java.name + ":Bundle"

        val SUITS = setOf("♣" /* clubs*/, "♦" /* diamonds*/, "♥" /* hearts*/, "♠" /*spades*/)
        val VALUES = setOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
        val DECK = SUITS.flatMap { suit ->
            VALUES.map { value -> Card(suit, value) }
        }

        fun List<Card>.find(value: String, suit: String): Card? {
            return find { it.value == value && it.suit == suit }
        }

        /** Use in conjunction with [Card.toBundle]  */
        fun fromBundle(bundle: Bundle): Card {
            val spec = bundle.getStringArray(ARGS_BUNDLE)
            return Card(spec!![0], spec[1])
        }
    }
}


/** Inflates and populates a [View] representing a [Card]  */
class CardView(layoutInflater: LayoutInflater, container: ViewGroup?) {
    val view: View = layoutInflater.inflate(R.layout.item_card_layout, container, false)
    private val textSuite: TextView

    init {
        textSuite = view.findViewById(R.id.label_center)
    }

    /**
     * Updates the view to represent the passed in card
     */
    fun bind(card: Card) {
        textSuite.text = card.suit
        view.setBackgroundResource(getColorRes(card))

        val cornerLabel = card.cornerLabel
    }

    @ColorRes
    private fun getColorRes(card: Card): Int {
        val shade = getShade(card)
        val color = getColor(card)
        return COLOR_MAP[color][shade]
    }

    private fun getShade(card: Card): Int {
        when (card.value) {
            "2", "6", "10", "A" -> return 2
            "3", "7", "J" -> return 3
            "4", "8", "Q" -> return 0
            "5", "9", "K" -> return 1
        }
        throw IllegalStateException("Card value cannot be $card.value")
    }

    private fun getColor(card: Card): Int {
        when (card.suit) {
            "♣" -> return 0
            "♦" -> return 1
            "♥" -> return 2
            "♠" -> return 3
        }
        throw IllegalStateException("Card suit cannot be $card.suit")
    }

    companion object {
        private val COLOR_MAP = arrayOf(
                intArrayOf(R.color.red_100, R.color.red_300, R.color.red_500, R.color.red_700),
                intArrayOf(R.color.blue_100, R.color.blue_300, R.color.blue_500, R.color.blue_700),
                intArrayOf(R.color.green_100, R.color.green_300, R.color.green_500,
                        R.color.green_700),
                intArrayOf(R.color.yellow_100, R.color.yellow_300, R.color.yellow_500,
                        R.color.yellow_700))
    }
}
