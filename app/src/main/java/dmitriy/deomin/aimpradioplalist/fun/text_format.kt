package dmitriy.deomin.aimpradioplalist.`fun`

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan

//
//        text = new SpannableString("Расписание от " + date);
//        text.setSpan(new StyleSpan(Typeface.BOLD), 14, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        text.setSpan(new RelativeSizeSpan(0.7f), 14, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


//часть текста жирным
fun Bold_text(text: String): SpannableStringBuilder {
    val t = SpannableStringBuilder(text)
    val end = text.indexOf(".")
    if (end > 0) {
        t.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
    return t
}