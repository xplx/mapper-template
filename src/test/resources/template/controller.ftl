package ${basePackage}.controller;
import ${basePackage}.model.entity.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import com.gzdsy.support.utils.R;
import com.gzdsy.support.utils.enums.StatusCode;
import com.gzdsy.support.utils.FieldErrorBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;

/**
*
* @author ${author}
* @date ${date}
*/
@Slf4j
@RestController
@RequestMapping("/school/${modelNameUpperCamel}")
@Api(tags = "${modelNameUpperCamel}Controller")
public class ${modelNameUpperCamel}Controller {
    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @ApiOperation(value = "获取信息（通过主键）")
    @GetMapping("/${modelNameLowerCamel}Info")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "id", value = "id", required = true)
    })
    public R<${modelNameUpperCamel}> ${modelNameLowerCamel}Info(@RequestParam String id) {
        ${modelNameUpperCamel} ${modelNameLowerCamel} = new ${modelNameUpperCamel}();
        try{
            ${modelNameLowerCamel} = ${modelNameLowerCamel}Service.findById(id);
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 获取信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 获取信息异常!");
        }
        return R.ok().setData(${modelNameLowerCamel});
    }

    @ApiOperation(value = "获取信息（list不分页）")
    @GetMapping("/list")
    public R<${modelNameUpperCamel}> list() {
        List<${modelNameUpperCamel}> list = new ArrayList<>();
        try{
            list = ${modelNameLowerCamel}Service.findAll();
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 获取信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 获取信息异常!");
        }
        return R.ok().setData(list);
    }

    @ApiOperation(value = "获取信息（list分页）")
    @GetMapping("/listPages")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageNum", value = "起始页"),
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "页大小")
    })
    public R<PageInfo<List<${modelNameUpperCamel}>>> listPages(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        List<${modelNameUpperCamel}> list = new ArrayList<>();
        try{
            PageHelper.startPage(pageNum, pageSize);
            list = ${modelNameLowerCamel}Service.findAll();
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 获取信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 获取信息异常!");
        }
        PageInfo pageInfo = new PageInfo(list);
        return R.ok().setData(pageInfo);
    }

    @ApiOperation(value = "添加信息")
    @PostMapping("/add")
    public R add(@Validated @RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return R.failure(StatusCode.FAILED, FieldErrorBuilder.build(bindingResult.getFieldErrors()));
        }
        try{
            ${modelNameLowerCamel}Service.saveSelectiveId(${modelNameLowerCamel});
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 保存信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 保存信息异常!");
        }
        return R.ok();
    }

    @ApiOperation(value = "修改信息")
    @PutMapping("/update")
    public R update(@Validated @RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return R.failure(StatusCode.FAILED, FieldErrorBuilder.build(bindingResult.getFieldErrors()));
        }
        try{
            ${modelNameLowerCamel}Service.updateByPrimaryKeyTb(${modelNameLowerCamel});
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 更新信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 更新信息异常!");
        }
        return R.ok();
    }

    @ApiOperation(value = "删除信息（主键）")
    @DeleteMapping("/delete")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "id", value = "id", required = true)
    })
    public R delete(@RequestParam String id) {
        try{
            ${modelNameLowerCamel}Service.deleteById(id);
        }catch (Exception e) {
            log.error("${modelNameUpperCamel}Controller 删除信息异常:{}", e);
            return R.failure(StatusCode.FAILED, "${modelNameUpperCamel}Controller 删除信息异常!");
        }
        return R.ok();
    }
}
