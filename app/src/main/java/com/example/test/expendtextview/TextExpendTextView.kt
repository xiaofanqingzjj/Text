package com.example.test.expendtextview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_expend_view.*

class TextExpendTextView : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expend_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        text.initWidth(getWindowManager().getDefaultDisplay().getWidth());
        // 设置最大行数
        text.setMaxLines(3);


        val text1 = "春江潮水连海平，海上明月共潮生。 \n" +
                "滟滟随波千万里，何处春江无月明！ \n" +
                "江流宛转绕芳甸，月照花林皆似霰； \n" +
                "空里流霜不觉飞，汀上白沙看不见。 \n" +
                "江天一色无纤尘，皎皎空中孤月轮。 \n" +
                "江畔何人初见月？江月何年初照人？ \n" +
                "人生代代无穷已，江月年年只相似。\n" +
                "春江潮水连海平，海上明月共潮生。 \n" +
                "滟滟随波千万里，何处春江无月明！ \n" +
                "江流宛转绕芳甸，月照花林皆似霰； \n" +
                "空里流霜不觉飞，汀上白沙看不见。 \n" +
                "江天一色无纤尘，皎皎空中孤月轮。 \n" +
                "江畔何人初见月？江月何年初照人？ \n" +
                "人生代代无穷已，江月年年只相似。"

        val content = "中国共产党是中国工人阶级的先锋队，同时是中国人民和中华民族的先锋队，是中国特色社会主义事业的领导核心，代表中国先进生产力的发展要求，代表中国先进文化的前进方向，代表中国最广大人民的根本利益。党的最高理想和最终目标是实现共产主义。";
        text.setExpendableText(text1);


    }


}