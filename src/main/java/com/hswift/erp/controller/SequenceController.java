package com.hswift.erp.controller;

import com.hswift.erp.service.sequence.SequenceService;
import com.hswift.erp.utils.BaseResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ji-sheng-hua 752*718*920
 */
@RestController
@RequestMapping(value = "/sequence")
public class SequenceController {
    private Logger logger = LoggerFactory.getLogger(SequenceController.class);

    @Resource
    private SequenceService sequenceService;

    /**
     * 单据编号生成接口
     * @param request
     * @return
     */
    @GetMapping(value = "/buildNumber")
    public BaseResponseInfo buildNumber(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String number = sequenceService.buildOnlyNumber();
            map.put("defaultNumber", number);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

}
