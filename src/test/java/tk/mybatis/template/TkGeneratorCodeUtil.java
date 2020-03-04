package tk.mybatis.template;


import tk.mybatis.template.util.TkGeneratorUtil;

/**
 * @author wuxiaopeng
 * @description: 生成TK模板代码
 * @date 2020/3/4 15:46
 */
public class TkGeneratorCodeUtil {
    public static void main(String[] args) {
        //生成表、可以使用数组同时生成多张表
        TkGeneratorUtil.genMoreTable("user_info");
        //自定义生成的类名
        //genCodeByCustomModelName("tb_user_info", "UserInfo");
    }
}
