package org.xiaofengcanyue.localandi18n;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.*;
import java.util.*;

public class AboutLocale {

    public static void main(String[] args) throws Exception{

        //useResourceBundle();

        //useReflectiveResourceBundle();

        //trackFormatPosition();

        //parseWithPosition();

        //useChoiceFormat();

        //formatWithNumber();

        //useLocaleCategory();

        useCollator();
    }

    /**
     * java7以后开始支持 IETF BCP 47(Tags for Identifying Languages)，它由以下几个部分组成：
     *  1、语言的名称
     *  2、语言的书写格式
     *  3、国家或地区的名称
     *  4、变体信息，用来描述语言或其方言的变体
     *  5、语言的扩展，用来描述语言的一些附加信息。每个扩展又由两部分组成：
     *      第一部分是单个字母的键；
     *      第二部分是2到8个字母或数字组成的值；
     *  6、最后一部分是私有标记。
     *
     *  语言标签以及基于它的Locale类的对象更多的时候只是一个标识符，它本身并不包含具体的本地化的能力。
     *  支持本地化的方法应该接受一个Locale类的对象作为参数，根据该对象所表示的区域来正确地产生相应的输出。
     */
    public static void useLocaleBuilder() throws Exception{
        Locale locale = new Locale.Builder().setLanguage("zh").setRegion("CN").setExtension('m',"myext").build();
        String tag = locale.toLanguageTag();
        System.out.println(tag);
    }

    public static class Messages_en_US extends ListResourceBundle{
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {"GREETING","Hello!"},
                    {"THANK_YOU","Thank you!"}
            };
        }
    }

    /**
     * ResourceBundle的getBundle方法各种不同的重载方式分成两类：
     *  第一类是基于资源包的基本名称、Locale类的对象和类加载器对象的查找方式；
     *  第二类是java6新增的通过ResourceBundle.Control类的对象来控制查找过程的查找方式。
     *
     * 第一种查找方式的关键在于根据资源包的基本名称和Locale类的对象来生成一个资源包对应的 Java类 和 属性文件 的名称查找序列。
     * 例如假设基本名称是"Messages"，所用的Locale类的对象的语言和国家或地区分别是"en"和"US"，那么对应的名称查找序列是：
     *  Messages_en_US、Messages_en和Messages
     * 首先会尝试通过指定的类加载器来查找并加载对应名称的Java类，接着会尝试查找属性文件。
     * 由于基本名称中可能带有名称空间，对应的属性文件名称是把候选名称中的"."替换为"/"之后的名称。
     *
     * ResourceBundle类的对象本身也是存在一定层次结构的。一个ResourceBundle类的对象有可能存在一个父ResourceBundle类的对象。
     * 子ResourceBundle类的对象会包含父ResourceBundle类对象中定义的键值对。
     * 根据上面提到的查找时的候选名称序列，出现在后面的查找到的ResourceBundle类的对象是之前的ResourceBundle类的对象的父亲。
     *
     *
     * 在java6之前，属性文件对应的PropertyResourceBundle类的对象只能从InputStream类的对象中创建，而且对应的属性文件只能采用ISO 8859-1的编码格式。
     * JDK自带的native2ascii工具可以把其他编码格式的属性文件转换成ISO 8859-1的格式。
     * 一般的做法是在构建过程中通过脚本的方式（如Apache Ant)调用native2ascii工具完成编码的转换。
     * 从java6开始，PropertyResourceBundle也可以从java.io.Reader类的对象中创建，因此可以兼容其他编码格式。
     *
     * @throws Exception
     */
    public static void useResourceBundle() throws Exception{

        String baseName = "org.xiaofengcanyue.localandi18n.AboutLocale$Messages";
        ResourceBundle bundleEn = ResourceBundle.getBundle(baseName,Locale.US);
        System.out.println(bundleEn.getString("GREETING"));

        String baseName1 = "org.xiaofengcanyue.localandi18n.Messages";
        ResourceBundle bundleCn = ResourceBundle.getBundle(baseName1,Locale.CHINA);
        System.out.println(new String(bundleCn.getString("GREETING").getBytes("ISO-8859-1"),"UTF-8"));

        String str = bundleCn.getString("THANK_YOU");

        for (char c:str.toCharArray()) {
            System.out.printf("%h\r\n",c);
        }

        String str1 = "谢谢";

        for (char c:str1.toCharArray()) {
            System.out.printf("%h\r\n",c);
        }

        System.out.println("\u8c22");

    }


    /**
     * 在使用了ResourceBundle.Control类的对象的情况下，资源包的查找过程与采用第一种查找方式相比，整体的流程是相似的。
     * 最开始getBundle方法会首先检查缓存。
     * 接着是通过getFormats方法来确定要查找的资源包的格式（java.class或java.properties或自定义）。
     * 然后通过getCandidateLocales方法来得到要搜索的Locale类的对象列表。
     * 列表中的Locale类的对象都会通过newBundle方法来尝试创建新的ResourceBundle对象。
     * 若上述尝试都失败，则会通过getFallbackLocale方法得到一个新的替代Locale类的对象，并再次尝试上面的查找过程。
     *
     */
    public static class ReflectiveResourceBundle extends ResourceBundle{

        private Class clazz;

        public ReflectiveResourceBundle(Class clazz){
            this.clazz = clazz;
        }

        public Object handleGetObject(String key){
            if (key == null){
                throw new NullPointerException();
            }
            try{
                Method method = clazz.getMethod(key);
                if(method == null){
                    return null;
                }
                return method.invoke(null);
            }catch (Exception e){
                return null;
            }
        }

        public Enumeration<String> getKeys(){
            Vector<String> result = new Vector<>();
            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                int mod = method.getModifiers();
                if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && method.getParameterTypes().length == 0){
                    result.add(method.getName());
                }
            }
            return result.elements();
        }
    }

    public static class ReflectiveResourceBundleControl extends ResourceBundle.Control{
        @Override
        public List<String> getFormats(String baseName) {
            if(baseName == null){
                throw new NullPointerException();
            }
            return Arrays.asList("reflection");
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if(baseName == null || locale == null || format ==null || loader == null){
                throw new NullPointerException();
            }
            ResourceBundle bundle = null;
            if(format.equals("reflection")){
                String bundleName = toBundleName(baseName,locale);
                try{
                    Class<?> clazz = loader.loadClass(bundleName);
                    return new ReflectiveResourceBundle(clazz);
                }catch (ClassNotFoundException ex){
                    return bundle;
                }
            }
            return bundle;
        }
    }

    public static class ReflectiveMessages_zh_CN{
        public static String greet(){
            return "你好， "+(Math.random() > 0.5 ?"先生":"女士");
        }
    }

    public static void useReflectiveResourceBundle(){
        String baseName = "org.xiaofengcanyue.localandi18n.AboutLocale$ReflectiveMessages";
        ReflectiveResourceBundleControl control = new ReflectiveResourceBundleControl();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName,Locale.CHINA,control);
        System.out.println(bundle.getString("greet"));
    }


    /**
     * 在java6之前，属性文件对应的PropertyResourceBundle类的对象只能从InputStream类的对象中创建，而且对应的属性文件只能采用ISO 8859-1的编码格式。
     * JDK自带的native2ascii工具可以把其他编码格式的属性文件转换成ISO 8859-1的格式。
     * 一般的做法是在构建过程中通过脚本的方式（如Apache Ant)调用native2ascii工具完成编码的转换。
     * 从java6开始，PropertyResourceBundle也可以从java.io.Reader类的对象中创建，因此可以兼容其他编码格式。
     *
     */
    public static class BetterResourceControl extends ResourceBundle.Control{
        private String encoding = "UTF-8";
        public BetterResourceControl(String encoding){
            if(encoding != null){
                this.encoding = encoding;
            }
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if("java.properties".equals(format)){
                String bundleName = toBundleName(baseName,locale);
                String resourceName = toResourceName(bundleName,"properties");
                InputStream stream = null;
                if(reload){
                    URL url = loader.getResource(resourceName);
                    if(url != null){
                        URLConnection connection = url.openConnection();
                        if(connection != null){
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                }else{
                    stream = loader.getResourceAsStream(resourceName);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream,encoding));
                return new PropertyResourceBundle(reader);
            }
            return super.newBundle(baseName,locale,format,loader,reload);
        }
    }

    public static class ResourceBundleLoader{
        public static ResourceBundle load(String baseName,Locale locale){
            BetterResourceControl control = new BetterResourceControl(null);
            return ResourceBundle.getBundle(baseName,locale,control);
        }
    }

    /**
     * 时间日期的本地化
     * 其实用SimpleDateFormat更方便
     */
    public static void trackFormatPosition(){

        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL,Locale.CHINA);
        Date date = new Date();
        StringBuffer result = new StringBuffer();
        FieldPosition dayField = new FieldPosition(DateFormat.DAY_OF_WEEK_FIELD);
        FieldPosition yearField = new FieldPosition(DateFormat.YEAR_FIELD);

        format.format(date,result,dayField);

        System.out.println(result.toString());

        String day = result.substring(dayField.getBeginIndex(),dayField.getEndIndex());

        System.out.printf("begin_index:%d,end_index:%d\r\n",dayField.getBeginIndex(),dayField.getEndIndex());

        System.out.println(day);

        String year = result.substring(yearField.getBeginIndex(),yearField.getEndIndex());

        System.out.printf("begin_index:%d,end_index:%d\r\n",yearField.getBeginIndex(),yearField.getEndIndex());

        System.out.println(year);

    }

    public static void parseWithPosition(){
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
        Date date = new Date();
        String dateStr = format.format(date);
        System.out.println(dateStr);
        String prefix = "== START ==";
        String toParse = prefix + dateStr + "== END ==";
        ParsePosition position = new ParsePosition(prefix.length());
        Date d = format.parse(toParse,position);
        int index = position.getIndex();

        System.out.println(index);

        System.out.println(d);

    }


    /**
     * 数字的格式化
     * @throws ParseException
     */
    public static void formatAndParseNumber() throws ParseException{
        NumberFormat format = NumberFormat.getNumberInstance();
        double num = 100.5;
        format.setMinimumFractionDigits(3);
        format.setMinimumIntegerDigits(5);
        format.format(num);
        String numStr = "523.34";
        format.setParseIntegerOnly(true);
        format.parse(numStr);
    }

    public static void useDecimalFormat(){
        NumberFormat format = NumberFormat.getNumberInstance();
        DecimalFormat df = null;
        if(format instanceof DecimalFormat){
            df = (DecimalFormat) format;
        }else{
            df = new DecimalFormat();
        }
        df.applyPattern("000,###");
        String str = df.format(3.14);
        System.out.println(str);
    }

    /**
     * 根据数字所处区间返回不同的字符串
     */
    public static void useChoiceFormat(){
        ChoiceFormat format = new ChoiceFormat(new double[] { 0 , 1 , ChoiceFormat.nextDouble(1)},new String[]{"no people","person","people"});
        int count = -4;
        String msg = count + " " + format.format(count);

        System.out.println(msg);
    }


    /**
     * 字符串的格式化和解析
     * 上面介绍的DateFormat、NumberFormat和ChoiceFormat类的对象可以作为其内部的子模式
     */
    public static void formatWithNumber(){
        String pattern = "购买了{0,number,integer}件商品，单价为{1,number,currency},合计：{2,number,\u00A4#,###.##}";
        MessageFormat format = new MessageFormat(pattern);
        int count = 3;
        double price = 1599.3;
        double total = price * count;
        String str = format.format(new Object[]{count,price,total});
        System.out.println(str);
    }


    /**
     * 在程序的国际化实现中，一般都使用当前java虚拟机默认的区域设置信息。
     * java7对默认的Locale类的对象进行了更进一步的细分，目前包括DISPLAY和FORMAT两种。
     * Locale类中的set/getDefault方法支持使用Locale.Category枚举类型中的值作为参数。
     *
     * @throws UnsupportedEncodingException
     */
    public static void useLocaleCategory() throws UnsupportedEncodingException {
        ResourceBundle bundle = ResourceBundle.getBundle("org.xiaofengcanyue.localandi18n.Messages");
        String str = new String(bundle.getString("GREETING").getBytes("ISO-8859-1"), Charset.forName("utf-8"));
        String msg = MessageFormat.format(str,new Object[]{"张三",new Date()});
        System.out.println(msg);
    }


    /**
     * 本地化字符串比较操作
     */
    public static void useCollator(){
        Collator collator = Collator.getInstance(Locale.US);
        collator.setStrength(Collator.PRIMARY);
        int i = collator.compare("abc","ABC");
        collator.setStrength(Collator.IDENTICAL);
        int j = collator.compare("abc","ABC");

        System.out.printf("i:%d,j:%d",i,j);

    }


}
