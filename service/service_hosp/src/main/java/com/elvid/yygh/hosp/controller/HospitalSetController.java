package com.elvid.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elvid.yygh.common.exception.YyghException;
import com.elvid.yygh.common.result.Result;
import com.elvid.yygh.common.utils.MD5;
import com.elvid.yygh.hosp.service.HospitalSetService;
import com.elvid.yygh.model.hosp.HospitalSet;
import com.elvid.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    // 1 查询医院设置表所有信息
    @GetMapping("findAll")
    @ApiOperation(value = "获取所有医院设置")
    public Result findAllHospitalSet() {
        return Result.ok(hospitalSetService.list());
    }

    // 2.删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 3 条件查询带分页
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("finaPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);

        // 构建条件
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();

        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        // 条件是否为空判断
        if (!StringUtils.isEmpty(hosname)) {
            hospitalSetQueryWrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)) {
            hospitalSetQueryWrapper.eq("hoscode", hoscode);
        }

        // 调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, hospitalSetQueryWrapper);

        return Result.ok(pageHospitalSet);
    }

    // 4 添加医院设置接口
    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态 1 代表可用 0 代表不可用
        hospitalSet.setStatus(1);
        // 设置秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 5 根据id获取医院设置
    @ApiOperation(value = "根据id获取医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        try {
            int a = 1 / 0;
        } catch (Exception e) {
            throw new YyghException("数学异常", 201);
        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    // 6 修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 7 批量删除医院设置接口
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean flag = hospitalSetService.removeByIds(idList);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 8 医院设置锁定和解锁; 只有医院设置处于解锁状态，才能和医院系统对接，实现数据操作
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHospSet/{id}/{status}")
    public Result lockHospSet(@PathVariable Long id,
                              @PathVariable Integer status) {
        // 根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    // 9 发送签名秘钥
    @ApiOperation(value = "发送签名秘钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hosname = hospitalSet.getHosname();
        // TODO 发送短信
        return Result.ok();

    }
}
