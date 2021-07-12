package com.hswift.erp.service.log;

import com.alibaba.fastjson.JSONObject;
import com.hswift.erp.constants.BusinessConstants;
import com.hswift.erp.datasource.entities.Log;
import com.hswift.erp.datasource.entities.LogExample;
import com.hswift.erp.datasource.mappers.LogMapper;
import com.hswift.erp.datasource.mappers.LogMapperEx;
import com.hswift.erp.datasource.vo.LogVo4List;
import com.hswift.erp.exception.hswiftException;
import com.hswift.erp.service.redis.RedisService;
import com.hswift.erp.service.user.UserService;
import com.hswift.erp.utils.StringUtil;
import com.hswift.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.hswift.erp.utils.Tools.getLocalIp;

@Service
public class LogService {
    private Logger logger = LoggerFactory.getLogger(LogService.class);
    @Resource
    private LogMapper logMapper;

    @Resource
    private LogMapperEx logMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;

    public Log getLog(long id)throws Exception {
        Log result=null;
        try{
            result=logMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            hswiftException.readFail(logger, e);
        }
        return result;
    }

    public List<Log> getLog()throws Exception {
        LogExample example = new LogExample();
        List<Log> list=null;
        try{
            list=logMapper.selectByExample(example);
        }catch(Exception e){
            hswiftException.readFail(logger, e);
        }
        return list;
    }

    public List<LogVo4List> select(String operation, Integer userId, String clientIp, Integer status, String beginTime, String endTime,
                                   String content, int offset, int rows)throws Exception {
        List<LogVo4List> list=null;
        try{
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            list=logMapperEx.selectByConditionLog(operation, userId, clientIp, status, beginTime, endTime,
                    content, offset, rows);
            if (null != list) {
                for (LogVo4List log : list) {
                    log.setCreateTimeStr(Tools.getCenternTime(log.getCreateTime()));
                }
            }
        }catch(Exception e){
            hswiftException.readFail(logger, e);
        }
        return list;
    }

    public Long countLog(String operation, Integer userId, String clientIp, Integer status, String beginTime, String endTime,
                        String content)throws Exception {
        Long result=null;
        try{
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            result=logMapperEx.countsByLog(operation, userId, clientIp, status, beginTime, endTime, content);
        }catch(Exception e){
            hswiftException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertLog(JSONObject obj, HttpServletRequest request) throws Exception{
        Log log = JSONObject.parseObject(obj.toJSONString(), Log.class);
        int result=0;
        try{
            result=logMapper.insertSelective(log);
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateLog(JSONObject obj, HttpServletRequest request)throws Exception {
        Log log = JSONObject.parseObject(obj.toJSONString(), Log.class);
        int result=0;
        try{
            result=logMapper.updateByPrimaryKeySelective(log);
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteLog(Long id, HttpServletRequest request)throws Exception {
        int result=0;
        try{
            result=logMapper.deleteByPrimaryKey(id);
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteLog(String ids, HttpServletRequest request)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        LogExample example = new LogExample();
        example.createCriteria().andIdIn(idList);
        int result=0;
        try{
            result=logMapper.deleteByExample(example);
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
        return result;
    }

    public void insertLog(String moduleName, String content, HttpServletRequest request)throws Exception{
        try{
            Long userId = userService.getUserId(request);
            if(userId!=null) {
                Log log = new Log();
                log.setUserId(userId);
                log.setOperation(moduleName);
                log.setClientIp(getLocalIp(request));
                log.setCreateTime(new Date());
                Byte status = 0;
                log.setStatus(status);
                log.setContent(content);
                logMapper.insertSelective(log);
            }
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
    }

    public void insertLogWithUserId(Long userId, Long tenantId, String moduleName, String content, HttpServletRequest request)throws Exception{
        try{
            if(userId!=null) {
                Log log = new Log();
                log.setUserId(userId);
                log.setOperation(moduleName);
                log.setClientIp(getLocalIp(request));
                log.setCreateTime(new Date());
                Byte status = 0;
                log.setStatus(status);
                log.setContent(content);
                log.setTenantId(tenantId);
                logMapper.insertSelective(log);
            }
        }catch(Exception e){
            hswiftException.writeFail(logger, e);
        }
    }
}