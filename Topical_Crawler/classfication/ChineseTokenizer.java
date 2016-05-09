package classfication;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class ChineseTokenizer {//分词采用es-ik实现,返回LinkedHashMap的分词

    public static Map<String, Long> segStr(String content){
        // 分词
//    	content=content.regex("^[\u4E00-\u9FFF]+$");
//    	System.out.println(content);
        Reader input = new StringReader(content);
        // 智能分词关闭（对分词的精度影响很大）
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lexeme = null;
        Map<String, Long> words = new LinkedHashMap<String, Long>();
        try {
            while ((lexeme = iks.next()) != null) {
                if (words.containsKey(lexeme.getLexemeText())) {
                    words.put(lexeme.getLexemeText(), words.get(lexeme.getLexemeText()) + 1);
                } else {
                    words.put(lexeme.getLexemeText(), 1L);
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
//        System.out.println(words);
        return words;
        
    }
}
