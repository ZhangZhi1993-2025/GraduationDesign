package domain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HtmlParser {
	private static Log log = LogFactory.getLog(HtmlParser.class);

    //private static final String contentID = "(=.?cid:[\\w\\s\\$\\.]*@[\\w]+)";
    private static final String contentID = "(cid:[\\w\\s\\$\\.]*@[\\w]+)";
    private static final Pattern contentIDPattern = Pattern.compile(contentID);

    private static final Pattern breakToNLPattern = Pattern.compile(
        "\\<[/]?br\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern pToDoubleNLPattern = Pattern.compile(
        "\\</p\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern divToDoubleNLPattern = Pattern.compile(
        "\\</div\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern hToDoubleNLPattern = Pattern.compile(
        "\\</h\\d\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern whiteSpaceRemovalPattern = Pattern.compile(
        "\\s+",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern trimSpacePattern = Pattern.compile("\n( )+",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern headerRemovalPattern = Pattern.compile(
        "\\<html[.|q\n|\r]*?\\<body(.|\n|\r)*?\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern stripTagsPattern = Pattern.compile(
        "\\<[\\s\\S]*?\\>",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern commentsRemovalPattern = Pattern.compile(
        "\\<!--(.|\n|\r)*?--\\>",
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern ScriptRemovalPattern = Pattern.compile(
    		"<script[\\S\\s]*?</script>",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern cssRemovalPattern = Pattern.compile(
            "<style[\\S\\s]*?</style>",
            Pattern.CASE_INSENSITIVE);
    
    private static String emailStr =
        "([\\w.\\-]*\\@([\\w\\-]+\\.*)+\\.[a-zA-Z0-9]{2,})";
    private static final Pattern emailPattern = Pattern.compile(emailStr);
    private static final Pattern emailPatternInclLink = Pattern.compile(
        "<a( |\\n)*?href=(\\\")?(mailto:)" + emailStr + "(.|\\n)*?</a>",
        Pattern.CASE_INSENSITIVE);
    private static String prot = "(http|https|ftp)";
    private static String punc = ".,:;?!\\-";
    private static String any = "\\S";
    private static String urlStr = "\\b" + "(" + "(\\w*(:\\S*)?@)?" + prot +
        "://" + "[" + any + "]+" + ")" + "(?=\\s|$)";

    /*
                 \\b  Start at word boundary
             (
             (\\w*(:\\S*)?@)?  [user:[pass]]@ - Construct
             prot + "://  protocol and ://
           ["+any+"]  match literaly anything...
             )
     (?=\\s|$)  ...until we find whitespace or end of String
     */
    private static final Pattern urlPattern = Pattern.compile(urlStr,
        Pattern.CASE_INSENSITIVE);
    private static String url_repairStr = "(.*://.*?)" + "(" + "(&gt;).*|" +
        "([" + punc + "]*)" + "(<br>)?" + ")$";

    /*
             (.*://.*?)"  "something" with ://
              (could be .*? but then the Pattern would match whitespace)
                 (
          (&gt;).*  a html-Encoded > followed by anything
                                                      |  or
             (["+punc+"]*)"  any Punctuation
            (<br>)? 0 or 1 trailing <br>
                 )$  end of String
     */
    private static final Pattern url_repairPattern = Pattern.compile(
        url_repairStr);
    private static final Pattern urlPatternInclLink = Pattern.compile(
        "<a( |\\n)*?href=(\\\")?" + urlStr + "(.|\\n)*?</a>",
        Pattern.CASE_INSENSITIVE);

    // TODO: Add more special entities - e.g. accenture chars such as ?

    /** Special entities recognized by restore special entities */
    private static String[] SPECIAL_ENTITIES = {
        "&lt;", "&gt;", "&amp;", "&nbsp;", "&#160;", "&quot;", "&apos;",
        "&aelig;", "&#230;", "&oslash;", "&#248;", "&aring;", "&#229;",
        "&AElig;", "&#198;", "&Oslash;", "&#216;", "&Aring;", "&#197;"
    };

    /** Normal chars corresponding to the defined special entities */
    private static char[] ENTITY_CHARS = {
        '<', '>', '&', ' ', ' ', '"', '\'', '?', '?', '?', '?', '?', '?', '?',
        '?', '?', '?', '?', '?'
    };

    /**
     * Strips html tags and removes extra spaces which occurs due
     * to e.g. indentation of the html and the head section, which does
     * not contain any textual information.
     * <br>
     * The conversion rutine does the following:<br>
     * 1. Removes the header from the html file, i.e. everything from
     *    the html tag until and including the starting body tag.<br>
     * 2. Replaces multiple consecutive whitespace characters with a single
     *    space (since extra whitespace should be ignored in html).<br>
     * 3. Replaces ending br tags with a single newline character<br>
     * 4. Replaces ending p, div and heading tags with two newlines characters;
     *    resulting in a single empty line btw. paragraphs.<br>
     * 5. Strips remaining html tags.<br>stripHtmlTags
     * <br>
     * NB: The tag stripping is done using a very simple regular expression,
     * which removes everything between &lt and &gt. Therefore too much text
     * could in some (hopefully rare!?) cases be removed.
     *
     * @param        s                Input string
     * @return        Input stripped for html tags
     * @author        Karl Peder Olesen (karlpeder)
     */
    public static String stripHtmlTags(String s) {
        // initial check of input:
        if (s == null) {
            return null;
        }

        // remove header
        s = cssRemovalPattern.matcher(s).replaceAll("");
        s = headerRemovalPattern.matcher(s).replaceAll("");
        // remove extra whitespace
        s = whiteSpaceRemovalPattern.matcher(s).replaceAll(" ");

        // replace br, p and heading tags with newlines
        s = breakToNLPattern.matcher(s).replaceAll("\n");
        s = pToDoubleNLPattern.matcher(s).replaceAll("\n\n");
        s = divToDoubleNLPattern.matcher(s).replaceAll("\n\n");
        s = hToDoubleNLPattern.matcher(s).replaceAll("\n\n");

        // strip remaining tags
        s = stripTagsPattern.matcher(s).replaceAll("");

        // tag stripping can leave some double spaces at line beginnings
        s = trimSpacePattern.matcher(s).replaceAll("\n").trim();

        return s;
    }

    /**
     * Strips html tags. The method used is very simple:
     * Everything between tag-start (&lt) and tag-end (&gt) is removed.
     * Optionaly br tags are replaced by newline and ending p tags with
     * double newline.
     *
     * @param        s                        input string
     * @param        breakToNl        if true, newlines are inserted for br and p tags
     * @return        output without html tags (null on error)
     * @author        karlpeder, 20030623
     *                         (moved from org.columba.mail.gui.message.util.DocumentParser)
     *
     * @deprecated        Please use the more advanced and correct
     *              @see stripHtmlTags(String) method
     */
    public static String stripHtmlTags(String s, boolean breakToNl) {
        // initial check of input:
        if (s == null) {
            return null;
        }

        if (breakToNl) {
            // replace <br> and </br> with newline
            s = breakToNLPattern.matcher(s).replaceAll("\n");

            // replace </p> with double newline
            s = pToDoubleNLPattern.matcher(s).replaceAll("\n\n");
        }

        // strip tags
        s = stripTagsPattern.matcher(s).replaceAll("");

        return s;
    }

    /**
     * Performs in large terms the reverse of
     * substituteSpecialCharacters (though br tags are not
     * converted to newlines, this should be handled separately).
     * More preciesly it changes special entities like
     * amp, nbsp etc. to their real counter parts: &, space etc.
     * <br>
     * This includes transformation of special (language specific) chars
     * such as the Danish ? ? ? ? ? ?.
     *
     * @param        s        input string
     * @return        output with special entities replaced with their
     *                         "real" counter parts (null on error)
     * @author  karlpeder, 20030623
     *                         (moved from org.columba.mail.gui.message.util.DocumentParser)
     */
    public static String restoreSpecialCharacters(String s) {
        // initial check of input:
        if (s == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer(s.length());
        StringReader sr = new StringReader(s);
        BufferedReader br = new BufferedReader(sr);
        String ss = null;

        try {
            while ( (ss = br.readLine()) != null) {
                int pos = 0;

                while (pos < ss.length()) {
                    char c = ss.charAt(pos);

                    if (c == '&') { 
                        // a special character is possibly found
                        if (ss.substring(pos).startsWith(
                            "&nbsp;&nbsp;&nbsp;&nbsp;") ||
                            ss.substring(pos).startsWith(
                            "&#160;&#160;&#160;&#160;")) {
                            // 4 spaces -> tab character
                            sb.append('\t');
                            pos = pos + 24;
                        }
                        else {
                            // seach among know special entities
                            boolean found = false;

                            for (int i = 0; i < SPECIAL_ENTITIES.length; i++) {
                                if (ss.substring(pos).startsWith(
                                    SPECIAL_ENTITIES[i])) {
                                    sb.append(ENTITY_CHARS[i]);
                                    pos = pos + SPECIAL_ENTITIES[i].length();
                                    found = true;

                                    break;
                                }
                            }

                            if (!found) {
                                if (ss.charAt(pos + 1) == '#') {
                                    char converted = (char) Integer.parseInt(ss.
                                        substring(pos + 2, pos + 5));
                                    sb.append(converted);
                                    pos = pos + 6;
                                    found = true;
                                }
                            }

                            if (!found) {
                                // unknown special char - just keep it as-is
                                sb.append(c);
                                pos++;
                            }
                        }
                    }
                    else {
                        // a "normal" char - keep it as is
                        sb.append(c);
                        pos++;
                    }
                }

                // end of line
                sb.append('\n');
            }
        }
        catch (Exception e) {
            log.error("Error restoring special characters: " +
                      e.getMessage());
            return null; // error
        }
        return sb.toString();
    }
    public static String safeHtml(String html){
    	html = ScriptRemovalPattern.matcher(html).replaceAll("");
    	html = cssRemovalPattern.matcher(html).replaceAll("");
    	return html;
    }
}