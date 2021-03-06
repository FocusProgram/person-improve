package com.jeecg.p3.goldeneggs.web;

import com.jeecg.p3.baseApi.service.BaseApiJwidService;
import com.jeecg.p3.baseApi.service.BaseApiSystemService;
import com.jeecg.p3.baseApi.util.WeixinUserUtil;
import com.jeecg.p3.goldeneggs.def.SystemGoldProperties;
import com.jeecg.p3.goldeneggs.entity.*;
import com.jeecg.p3.goldeneggs.exception.GoldeneggsException;
import com.jeecg.p3.goldeneggs.exception.GoldeneggsExceptionEnum;
import com.jeecg.p3.goldeneggs.service.*;
import com.jeecg.p3.goldeneggs.verify.entity.WxActGoldeneggsVerify;
import com.jeecg.p3.goldeneggs.verify.service.WxActGoldeneggsVerifyService;
import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.base.vo.WeixinDto;
import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.jeecgframework.p3.core.utils.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/goldeneggs/new")
public class NewGoldeneggController {
    public final static Logger LOG = LoggerFactory
            .getLogger(NewGoldeneggController.class);
    @Autowired
    private WxActGoldeneggsRegistrationService wxActGoldeneggsRegistrationService;
    @Autowired
    private WxActGoldeneggsService wxActGoldeneggsService;
    @Autowired
    private WxActGoldeneggsRelationService wxActGoldeneggsRelationService;
    @Autowired
    private WxActGoldeneggsPrizesService wxActGoldeneggsPrizesService;
    @Autowired
    private WxActGoldeneggsAwardsService wxActGoldeneggsAwardsService;
    @Autowired
    private WxActGoldeneggsRecordService wxActGoldeneggsRecordService;
    @Autowired
    private BaseApiJwidService baseApiJwidService;
    @Autowired
    private BaseApiSystemService baseApiSystemService;
    @Autowired
    private WxActGoldeneggsShareRecordService wxActGoldeneggsShareRecordService;
    @Autowired
    private WxActGoldeneggsVerifyService wxActGoldeneggsVerifyService;

    private static String domain = SystemGoldProperties.domain;

    /**
     * ???????????????
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toGoldenegg", method = {RequestMethod.GET, RequestMethod.POST})
    public void toGoldenegg(@ModelAttribute WeixinDto weixinDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("toGoldenegg parameter WeixinDto={}.", new Object[]{weixinDto});
        String viewName = "goldeneggs/vm/index.vm";
        VelocityContext velocityContext = new VelocityContext();
        try {
            validateWeixinDtoParam(weixinDto);
            String actId = weixinDto.getActId();
            String openid = weixinDto.getOpenid();
            String jwid = weixinDto.getJwid();
            String appid = weixinDto.getAppid();
            WxActGoldeneggs wxActGoldeneggs = wxActGoldeneggsService.queryById(actId);
            Integer joinNum = wxActGoldeneggsRegistrationService.queryCountByActId(actId);
            if (wxActGoldeneggs == null) {
                throw new GoldeneggsException(GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "???????????????");
            }
            long date = new Date().getTime();
            if (date < wxActGoldeneggs.getStarttime().getTime()) {
                //throw new GoldeneggsException(GoldeneggsExceptionEnum.ACT_BARGAIN_NO_START, "??????????????????");
                velocityContext.put("act_Status", false);
                velocityContext.put("act_Status_Msg", "???????????????");
            }
            if (date > wxActGoldeneggs.getEndtime().getTime()) {
                //throw new GoldeneggsException(GoldeneggsExceptionEnum.ACT_BARGAIN_END, "???????????????");
                velocityContext.put("act_Status", false);
                velocityContext.put("act_Status_Msg", "???????????????");
            }
            //validateActDate(wxActGoldeneggs);
            //?????????????????? 0????????????????????????  1????????????????????????
            if ("0".equals(wxActGoldeneggs.getFoucsUserCanJoin())) {
                WeixinUserUtil.setWeixinDto(weixinDto, null);
                if (!"1".equals(weixinDto.getSubscribe())) {
//						throw new GoldeneggsException(
//								GoldeneggsExceptionEnum.ARGUMENT_ERROR,"???????????????????????????");
                    velocityContext.put("whetherSubscribe", "0");
                } else {
                    velocityContext.put("whetherSubscribe", "1");
                }
            } else {
                velocityContext.put("whetherSubscribe", "1");
            }

            // ??????????????????????????????????????????????????????????????????????????????
            List<WxActGoldeneggsRelation> relationList = wxActGoldeneggsRelationService.queryPrizeAndAward(actId);
            // ???????????????
            List<WxActGoldeneggsPrizes> prizesList = wxActGoldeneggsPrizesService.queryByActId(actId);
            // ????????????????????????
            List<WxActGoldeneggsRecord> personalWinList = wxActGoldeneggsRecordService.queryPersonalWin(openid, actId);
            // ????????????????????????
            List<WxActGoldeneggsRecord> winList = wxActGoldeneggsRecordService.queryByWin();

            Integer count = null;//????????????????????????
            Integer numPerDay = null;//???????????????????????????
            Integer awardsNum = null;//?????????????????????
            Integer remainNumDay = null;//??????????????????????????????
            Integer shareNumflag = null;//???????????????????????????
            WxActGoldeneggsRegistration registration = wxActGoldeneggsRegistrationService.getOpenid(openid, actId);
            count = wxActGoldeneggs.getCount();
            numPerDay = wxActGoldeneggs.getNumPerDay();
            if (registration == null) {
                //????????????????????????
                awardsNum = count;
                //????????????????????????
                remainNumDay = numPerDay;
            }
            if (registration != null) {
                SimpleDateFormat sb = new SimpleDateFormat("yyyyMMdd");
                String update = sb.format(new Date());
                if (update.equals(registration.getUpdateTime())) {
                    //????????????????????????
                    remainNumDay = wxActGoldeneggs.getNumPerDay() - registration.getRemainNumDay();// ?????????????????????
                } else {
                    //????????????????????????
                    remainNumDay = wxActGoldeneggs.getNumPerDay();
                }
                //????????????????????????
                awardsNum = count - registration.getAwardsNum();
            }
            if (awardsNum < 1) {
                awardsNum = 0;
            }
            if (remainNumDay < 1) {
                remainNumDay = 0;
            }
            if (count != 0) {
                if (remainNumDay > awardsNum) {
                    remainNumDay = awardsNum;
                }
            }

            if (StringUtils.isNotEmpty(wxActGoldeneggs.getTemplateCode())) {
                viewName = "goldeneggs/template/" + wxActGoldeneggs.getTemplateCode() + "/vm/index.vm";
            }

            //--update-begin---author:lsq---date:20181010-----for:???????????????---------------
            //??????????????????????????????
            if (count != 0) {
                if (registration != null && count <= registration.getAwardsNum()) {
                    velocityContext.put("countFlag", "1"); //??????????????????
                } else {
                    velocityContext.put("countFlag", "0"); //??????????????????
                }
            } else {
                velocityContext.put("countFlag", "0"); //??????????????????
            }

            //int dayBargainRecordCount = wxActJiugonggeRecordService.queryBargainRecordCountByOpenidAndActidAndJwid(weixinDto.getOpenid(), weixinDto.getActId(), weixinDto.getJwid(), currDate);
            //????????????????????????????????? 0??????1??????
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            if ("0".equals(wxActGoldeneggs.getExtraLuckyDraw())) {
                //??????????????????????????????
                List<WxActGoldeneggsShareRecord> wxActGoldeneggsShareRecord = wxActGoldeneggsShareRecordService.queryShareRecordByDate(actId, openid, sd.format(new Date()), "");
                if (wxActGoldeneggsShareRecord.size() > 0) {
                    //?????????
                    velocityContext.put("shareFlag", "0");
                    if (registration != null) {
                        shareNumflag = wxActGoldeneggs.getNumPerDay() - registration.getRemainNumDay();
                        if (shareNumflag < 0) {
                            velocityContext.put("shareNumflag", shareNumflag);
                        }
                    }
                } else {
                    //?????????
                    velocityContext.put("shareFlag", "1");
                }
            } else {
                velocityContext.put("shareFlag", "1");
            }
            //--update-end---author:lsq---date:20181010-----for:???????????????---------------


            velocityContext.put("count", count);//????????????????????????
            velocityContext.put("numPerDay", numPerDay);//???????????????????????????
            velocityContext.put("awardsNum", awardsNum);//?????????????????????
            velocityContext.put("remainNumDay", remainNumDay);//??????????????????????????????
            velocityContext.put("relationList", relationList);//????????????????????? ,????????????????????????????????????????????????
            velocityContext.put("prizesList", prizesList);// ???????????????
            velocityContext.put("personalWinList", personalWinList);// ????????????????????????
            velocityContext.put("winList", winList);// ??????????????????
            velocityContext.put("goldeneggs", wxActGoldeneggs);//?????????-????????????
            velocityContext.put("weixinDto", weixinDto);
            String Hdurl = wxActGoldeneggs.getHdurl().replace("${domain}", domain);
            velocityContext.put("hdUrl", Hdurl); //????????????URL
            velocityContext.put("appId", appid);// ?????????????????????????????????
            velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);// ?????????????????????????????????
            velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);// ?????????????????????????????????
            velocityContext.put("signature", WeiXinHttpUtil.getRedisSignature(request, jwid));// ???????????????????????????1
            velocityContext.put("doMain", "/upload/img/goldeneggs");
            velocityContext.put("huodong_bottom_copyright", baseApiSystemService.getHuodongLogoBottomCopyright(wxActGoldeneggs.getCreateBy()));
            String qrcodeUrl = baseApiJwidService.getQrcodeUrl(weixinDto.getJwid());
            velocityContext.put("qrcodeUrl", qrcodeUrl);
            velocityContext.put("joinNum", joinNum);
        } catch (GoldeneggsException e) {
            e.printStackTrace();
            LOG.error("goldeneggs/new toGoldenegg error:{}", e.getMessage());
            velocityContext.put("errCode", e.getDefineCode());
            velocityContext.put("errMsg", e.getMessage());
            viewName = chooseErrorPage(e.getDefineCode());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("goldeneggs/new toGoldenegg error:{}", e);
            velocityContext.put("errCode", GoldeneggsExceptionEnum.SYS_ERROR.getErrCode());
            velocityContext.put("errMsg", GoldeneggsExceptionEnum.SYS_ERROR.getErrChineseMsg());
            viewName = "system/vm/error.vm";
        }
        try {
            ViewVelocity.view(request, response, viewName, velocityContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param errorCode
     * @return
     */
    private String chooseErrorPage(String errorCode) {
        if (errorCode.equals("02007")) {
            return "system/vm/before.vm";
        } else if (errorCode.equals("02008")) {
            return "system/vm/over.vm";
        } else {
            return "system/vm/error.vm";
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/toCheck", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson toCheck(@ModelAttribute WeixinDto weixinDto,
                            HttpServletRequest request, HttpServletResponse response)
            throws ParseException {
        AjaxJson j = new AjaxJson();
        Map<String, Object> attributes = new HashMap<String, Object>();
        try {
            String actId = request.getParameter("actId");
            String jwid = request.getParameter("jwid");
            String openid = request.getParameter("openid");
            WxActGoldeneggs wxActGoldeneggs = wxActGoldeneggsService.queryById(actId);
            if (wxActGoldeneggs == null) {
                throw new GoldeneggsException(
                        GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "???????????????");
            }
            if (!jwid.equals(wxActGoldeneggs.getJwid())) {
                throw new GoldeneggsException(
                        GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR,
                        "?????????????????????????????????");
            }
            //??????????????????????????????
            Date starttime = wxActGoldeneggs.getStarttime();
            Date endtime = wxActGoldeneggs.getEndtime();
            if (new Date().before(starttime) || new Date().after(endtime)) {
                j.setObj("5");
                j.setSuccess(false);
                return j;
            }
            //??????????????????1????????????????????????0????????????????????????
            if ("1".equals(wxActGoldeneggs.getFoucsUserCanJoin())) {
                WeixinUserUtil.setWeixinDto(weixinDto, null);
                if (!"1".equals(weixinDto.getSubscribe())) {
                    attributes.put("whetherSubscribe", "0");
                    j.setAttributes(attributes);
                    j.setSuccess(false);
                } else {
                    attributes.put("whetherSubscribe", "1");
                    j.setAttributes(attributes);
                }
            }
            //?????????????????????????????????
            int extraCount = 0;
            if ("0".equals(wxActGoldeneggs.getExtraLuckyDraw())) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                List<WxActGoldeneggsShareRecord> wxActGoldeneggsShareRecord = wxActGoldeneggsShareRecordService.queryShareRecordByDate(actId, openid, sd.format(new Date()), "");
                if (wxActGoldeneggsShareRecord.size() > 0) {
                    extraCount = 1;
                }
            }
            WxActGoldeneggsRegistration wxActGoldeneggsRegistration = wxActGoldeneggsRegistrationService
                    .getOpenid(openid, actId);
            if (wxActGoldeneggs.getCount() != 0) {
                if (wxActGoldeneggsRegistration != null) {
                    Integer remainNumDays = null;
                    Integer awardsNum = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String date = sdf.format(new Date());
                    if (wxActGoldeneggsRegistration.getUpdateTime().equals(date)) {
                        remainNumDays = wxActGoldeneggs.getNumPerDay() + extraCount - wxActGoldeneggsRegistration
                                .getRemainNumDay();// ????????????????????????
                        if (remainNumDays < 1) {
                            j.setObj("3");
                            attributes.put("title", "???????????????????????????");
                            j.setAttributes(attributes);
                            j.setSuccess(false);
                        }
                    }
                    awardsNum = wxActGoldeneggsRegistration.getAwardsNum();// ?????????????????????
                    if (awardsNum >= wxActGoldeneggs.getCount()) {
                        j.setObj("4");
                        attributes.put("title", "????????????????????????");
                        j.setAttributes(attributes);
                        j.setSuccess(false);
                    }
                }
            } else {
			/*	if("1".equals(wxActGoldeneggs.getFoucsUserCanJoin())){
					setWeixinDto(weixinDto);		
					if(!"1".equals(weixinDto.getSubscribe())){
						//?????????
						attributes.put("whetherSubscribe","0");
						j.setAttributes(attributes);
						j.setSuccess(false);	
					}else{
						//?????????
						attributes.put("whetherSubscribe","1");
						j.setAttributes(attributes);
						j.setSuccess(false);	
					}
				}*/
                if (wxActGoldeneggsRegistration != null) {
                    Integer remainNumDays = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String date = sdf.format(new Date());
                    if (wxActGoldeneggsRegistration.getUpdateTime().equals(date)) {
                        remainNumDays = wxActGoldeneggs.getNumPerDay() + extraCount - wxActGoldeneggsRegistration
                                .getRemainNumDay();// ????????????????????????
                        if (remainNumDays < 1) {
                            j.setObj("3");
                            attributes.put("title", "???????????????????????????");
                            j.setAttributes(attributes);
                            j.setSuccess(false);
                        }
                    }
                }
            }
        } catch (GoldeneggsException e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setObj("4");
            attributes.put("title", e.getMessage());
            j.setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setObj("4");
            attributes.put("title", "????????????!");
            LOG.info(e.toString());
        }
        return j;
    }

    /**
     * ?????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */

    @RequestMapping(value = "/toAward", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson toAward(@ModelAttribute WeixinDto weixinDto,
                            HttpServletRequest request, HttpServletResponse response)
            throws ParseException {
        AjaxJson j = new AjaxJson();
        Map<String, Object> attributes = new HashMap<String, Object>();
        try {
            String actId = request.getParameter("actId");
            String jwid = request.getParameter("jwid");
            String openid = request.getParameter("openid");
            WxActGoldeneggs queryById = wxActGoldeneggsService.queryById(actId);
            if (queryById == null) {
                throw new GoldeneggsException(
                        GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "???????????????");
            }
            if (!jwid.equals(queryById.getJwid())) {
                throw new GoldeneggsException(
                        GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR,
                        "?????????????????????????????????");
            }
            //??????????????????
            String prizeStatus = queryById.getPrizeStatus();
            //0:?????????????????????;1????????????????????????
            if ("0".equals(prizeStatus)) {
                j = wxActGoldeneggsRegistrationService.prizeRecordNew(weixinDto, j);// ?????????????????????????????????
            } else {
                //????????????????????????
                List<WxActGoldeneggsRecord> wxActGoldeneggsRecords = wxActGoldeneggsRecordService.queryByActidAndOpenidAndPrizesStatus(actId, openid, "1");
                if (wxActGoldeneggsRecords != null && wxActGoldeneggsRecords.size() > 0) {
                    j = wxActGoldeneggsRegistrationService.noPrizeRecordNew(weixinDto, j);// ??????????????????????????????
                } else {
                    j = wxActGoldeneggsRegistrationService.prizeRecordNew(weixinDto, j);// ?????????????????????????????????
                }
            }

        } catch (GoldeneggsException e) {
            j.setSuccess(false);
            attributes.put("title", e.getMessage());
            j.setAttributes(attributes);
            j = wxActGoldeneggsRegistrationService.noPrizeRecordNew(weixinDto, j);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            attributes.put("title", "???????????????");
            j = wxActGoldeneggsRegistrationService.noPrizeRecordNew(weixinDto, j);
        }
        return j;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/saveGoldEggPrize", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson saveGoldEggPrize(@ModelAttribute WeixinDto weixinDto,
                                     HttpServletRequest request, HttpServletResponse response)
            throws ParseException {
        AjaxJson j = new AjaxJson();
        try {
            String mobile = request.getParameter("mobile");
            String username = request.getParameter("username");
            String address = request.getParameter("address");
            String code = request.getParameter("code");
            WxActGoldeneggsRecord queryByCode = wxActGoldeneggsRecordService
                    .queryByCode(code);
            queryByCode.setPhone(mobile);
            queryByCode.setAddress(address);
            queryByCode.setRealname(username);
            wxActGoldeneggsRecordService.doEdit(queryByCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    /**
     * ????????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */

    @RequestMapping(value = "/toMyPrize", method = {RequestMethod.GET,
            RequestMethod.POST})
    public void toMyPrize(@ModelAttribute WeixinDto weixinDto,
                          HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LOG.info("toYaoqian parameter WeixinDto={}.",
                new Object[]{weixinDto});
        String jwid = weixinDto.getJwid();
        String openid = weixinDto.getOpenid();
        String actId = weixinDto.getActId();
        String code = request.getParameter("code");
        String viewName = "goldeneggs/vm/prizename.vm";
        VelocityContext velocityContext = new VelocityContext();
        String userAddress = null;
        String userName = null;
        String userMobile = null;
        List<WxActGoldeneggsRecord> queryLists = wxActGoldeneggsRecordService
                .queryMyList(openid, actId);
        List<WxActGoldeneggsRecord> queryByCodes = new ArrayList<WxActGoldeneggsRecord>();
        for (WxActGoldeneggsRecord list : queryLists) {
            String codes = list.getCode();
            if (codes != null) {
                WxActGoldeneggsRecord queryByCode = wxActGoldeneggsRecordService
                        .queryByCode(codes);
                userAddress = queryByCode.getAddress();
                userName = queryByCode.getRealname();
                userMobile = queryByCode.getPhone();
                queryByCodes.add(queryByCode);
            }
        }
        velocityContext.put("code", code);
        velocityContext.put("queryList", queryByCodes);
        velocityContext.put("weixinDto", weixinDto);
        velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);// ?????????????????????????????????
        velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);// ?????????????????????????????????
        velocityContext.put("signature",
                WeiXinHttpUtil.getRedisSignature(request, jwid));// ???????????????????????????1
        ViewVelocity.view(request, response, viewName, velocityContext);

    }

    /**
     * ????????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/toAllPrize", method = {RequestMethod.GET,
            RequestMethod.POST})
    public void toAllPrize(@ModelAttribute WeixinDto weixinDto,
                           HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LOG.info("toYaoqian parameter WeixinDto={}.",
                new Object[]{weixinDto});
        String jwid = weixinDto.getJwid();
        String openid = weixinDto.getOpenid();
        String actId = weixinDto.getActId();
        String viewName = "goldeneggs/vm/allprizename.vm";
        VelocityContext velocityContext = new VelocityContext();

        List<WxActGoldeneggsRecord> queryLists = wxActGoldeneggsRecordService
                .queryList(actId);
        List<WxActGoldeneggsRecord> queryByCodes = new ArrayList<WxActGoldeneggsRecord>();
        for (WxActGoldeneggsRecord list : queryLists) {
            String codes = list.getCode();
            if (codes != null) {
                WxActGoldeneggsRecord queryByCode = wxActGoldeneggsRecordService
                        .queryByCode(codes);
                queryByCodes.add(queryByCode);
            }
        }
        velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);// ?????????????????????????????????
        velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);// ?????????????????????????????????
        velocityContext.put("signature",
                WeiXinHttpUtil.getRedisSignature(request, jwid));// ???????????????????????????1
        velocityContext.put("queryList", queryByCodes);
        velocityContext.put("weixinDto", weixinDto);
        ViewVelocity.view(request, response, viewName, velocityContext);

    }

    /**
     * ????????????????????????????????????
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    @RequestMapping(value = "/toUpdateMessage", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson toUpdateMessage(@ModelAttribute WeixinDto weixinDto,
                                    HttpServletRequest request, HttpServletResponse response) {
        LOG.info("toUpdateMessage parameter WeixinDto={}.",
                new Object[]{weixinDto});
        AjaxJson j = new AjaxJson();
        String jwid = weixinDto.getJwid();
        String openid = weixinDto.getOpenid();
        String actId = weixinDto.getActId();
        String code = request.getParameter("code");
        try {
            WxActGoldeneggsRecord queryByCode = wxActGoldeneggsRecordService
                    .queryByCode(code);

            String userAddress = null;
            String userName = null;
            String userMobile = null;
            if (queryByCode != null) {
                userAddress = queryByCode.getAddress();
                userName = queryByCode.getRealname();
                userMobile = queryByCode.getPhone();
            }
            Map<String, Object> mm = new HashMap<String, Object>();
            mm.put("userName", userName);
            mm.put("userAddress", userAddress);
            mm.put("userMobile", userMobile);
            j.setAttributes(mm);
            j.setObj("iscode");
        } catch (Exception e) {
            j.setSuccess(false);
        }
        return j;
    }

    /**
     * ????????????id???openid????????????
     *
     * @param weixinDto
     */
    private void validateWeixinDtoParam(WeixinDto weixinDto) {
        if (StringUtils.isEmpty(weixinDto.getActId())) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ARGUMENT_ERROR, "??????ID????????????");
        }
        if (StringUtils.isEmpty(weixinDto.getOpenid())) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ARGUMENT_ERROR, "?????????openid????????????");
        }
        if (StringUtils.isEmpty(weixinDto.getJwid())) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ARGUMENT_ERROR, "??????ID????????????");
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void validateActDate(WxActGoldeneggs wxActGoldeneggs) {
        Date date = new Date();
        if (wxActGoldeneggs == null) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ACT_BARGAIN_END, "???????????????");
        } else if (date.before(wxActGoldeneggs.getStarttime())) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ARGUMENT_ERROR, "???????????????");
        } else if (date.after(wxActGoldeneggs.getEndtime())) {
            throw new GoldeneggsException(
                    GoldeneggsExceptionEnum.ARGUMENT_ERROR, "???????????????");
        }
    }


    /**
     * ??????????????????
     */
    @RequestMapping("/queryWinList")
    @ResponseBody
    public AjaxJson queryWinList(HttpServletRequest request,
                                 @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                                 @RequestParam(required = false, value = "pageSize", defaultValue = "30") int pageSize) throws Exception {
        AjaxJson json = new AjaxJson();
        try {
            String actId = request.getParameter("actId");
            Map<String, Object> attribute = new HashMap<String, Object>();
            // ????????????????????????
            int count = wxActGoldeneggsRecordService.queryCountByWin(actId);
            List<WxActGoldeneggsRecord> winList = wxActGoldeneggsRecordService.queryByWinAndPage(actId, pageNo * pageSize, pageSize);
            attribute.put("count", count - (pageNo * pageSize));
            json.setObj(winList);
            json.setAttributes(attribute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * ??????????????????
     */
    @RequestMapping("/queryGoldeneggsRecord")
    @ResponseBody
    public AjaxJson queryGoldeneggsRecord(HttpServletRequest request) throws Exception {
        AjaxJson json = new AjaxJson();
        try {
            String code = request.getParameter("code");
            WxActGoldeneggsRecord goldeneggsRecord = wxActGoldeneggsRecordService.queryByCode(code);
            json.setSuccess(true);
            json.setObj(goldeneggsRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * @param weixinDto
     * @param request
     * @param response
     * @return
     * @?????????????????????
     */
    @RequestMapping(value = "/fxCallback", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson fxCallback(@ModelAttribute WeixinDto weixinDto, HttpServletRequest request, HttpServletResponse response) {
        AjaxJson j = new AjaxJson();
        try {
            String actId = weixinDto.getActId();
            String openid = weixinDto.getOpenid();
            String type = request.getParameter("type");
            //?????????????????????????????????
            WxActGoldeneggsShareRecord shareRecord = new WxActGoldeneggsShareRecord();
            shareRecord.setActId(actId);
            shareRecord.setOpenid(openid);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            shareRecord.setRelDate(sd.format(new Date()));
            shareRecord.setType(type);
            shareRecord.setCreateTime(new Date());
            wxActGoldeneggsShareRecordService.doAdd(shareRecord);
            //????????????
            j.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    /**
     * ?????????????????????
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getVerificationUrl", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson getVerificationUrl(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        AjaxJson j = new AjaxJson();
        try {
            //---update-begin-zhaofei---Date:20190812----for:???????????????url----
            String hdurl = baseApiSystemService.getProjectHdurlByCode("goldeneggsVerification");
            //---update-end-zhaofei---Date:20190812----for:???????????????url----

            //---update-begin-zhaofei---Date:20190814----for:???????????????????????????????????????----
            String cardPsd = request.getParameter("cardPsd");
            String actId = request.getParameter("actId");
            String jwid = request.getParameter("jwid");
            if (hdurl != null) {
                hdurl = hdurl + "&awd=" + cardPsd;
                hdurl = hdurl + "&actId=" + actId;
                hdurl = hdurl + "&jwid=" + jwid;
            }

            //---update-begin-zhaofei---Date:20190812----for:???????????????url??????----
            String hdUrl = hdurl.replace("${domain}", domain);
            //---update-end-zhaofei---Date:20190812----for:???????????????url??????----

            String shortUrl = WeiXinHttpUtil.getShortUrl(hdUrl, SystemGoldProperties.defaultJwid);
            //hdurl="http://192.168.1.146:8080/P3-Web/goldeneggs/new/toVerificationreview.do?actId="+actId+"&jwid="+jwid+"&openid=123456&appid=wx6596a35fea9085d4&awd="+cardPsd;
            LOG.info("?????????????????????:" + hdUrl);

            j.setSuccess(true);
            j.setObj(hdUrl);
            if (shortUrl != null) {
                j.setObj(shortUrl);
            }
            //---update-end-zhaofei---Date:20190814----for:???????????????????????????????????????----
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
        }
        return j;
    }

    /**
     * ???????????????
     * ??????????????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toVerificationreview", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public void getVerificationreview(@ModelAttribute WeixinDto weixinDto,
                                      HttpServletRequest request, HttpServletResponse response) {
        // ????????????
        //http://192.168.1.146:8080/P3-Web/goldeneggs/new/toVerificationreview.do?actId=4028811266a3cdde0166a3f446f70008&jwid=gh_20419b74f848&openid=123456&appid=wx6596a35fea9085d4&awd=jWHBH9BT0raV
        validateWeixinDtoParam(weixinDto);
        String cardPsd = request.getParameter("awd");
        String actId = weixinDto.getActId();
        String openid = weixinDto.getOpenid();
        WxActGoldeneggs wxActGoldeneggs = wxActGoldeneggsService.queryById(actId);
        String viewName = "";
        VelocityContext velocityContext = new VelocityContext();
        try {
            if (StringUtils.isEmpty(wxActGoldeneggs.getTemplateCode())) {
                throw new GoldeneggsException(GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "???????????????");
            }
            if (StringUtils.isEmpty(cardPsd)) {
                throw new GoldeneggsException(GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "?????????????????????");
            }
            WxActGoldeneggsVerify verify = wxActGoldeneggsVerifyService.queryByOpenId(openid, actId);
            if (verify != null && "0".equals(verify.getStatus())) {
                viewName = "goldeneggs/template/" + wxActGoldeneggs.getTemplateCode() + "/vm/coupon.vm";
                WxActGoldeneggsVerify veri = wxActGoldeneggsVerifyService.queryAllGoldeneggs(actId, cardPsd);
                if (veri != null) {
                    velocityContext.put("veri", veri);
                    velocityContext.put("verify", verify);
                } else {
                    throw new GoldeneggsException(GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "??????????????????????????????????????????");
                }
            } else {
                throw new GoldeneggsException(GoldeneggsExceptionEnum.DATA_NOT_EXIST_ERROR, "???????????????????????????????????????????????????");
            }
        } catch (GoldeneggsException e) {
            e.printStackTrace();
            LOG.error("goldeneggs/new toGoldenegg error:{}", e.getMessage());
            velocityContext.put("errCode", e.getDefineCode());
            velocityContext.put("errMsg", e.getMessage());
            viewName = chooseErrorPage(e.getDefineCode());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("goldeneggs/new toGoldenegg error:{}", e);
            velocityContext.put("errCode", GoldeneggsExceptionEnum.SYS_ERROR.getErrCode());
            velocityContext.put("errMsg", GoldeneggsExceptionEnum.SYS_ERROR.getErrChineseMsg());
            viewName = "system/vm/error.vm";
        }
        try {
            ViewVelocity.view(request, response, viewName, velocityContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/doVerify", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson doVerify(@ModelAttribute WxActGoldeneggsRecord Record,
                             HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        AjaxJson j = new AjaxJson();
        try {
            if (StringUtils.isNotEmpty(Record.getActId()) && StringUtils.isNotEmpty(Record.getCode()) && StringUtils.isNotEmpty(Record.getOpenid())) {
                WxActGoldeneggsRecord recor = wxActGoldeneggsRecordService.queryByActIdAndCode(Record.getActId(), Record.getCode());
                //???????????????id
                WxActGoldeneggsVerify Verify = wxActGoldeneggsVerifyService.queryByOpenId(Record.getOpenid(), Record.getActId());
                if (Verify != null && "0".equals(Verify.getStatus())) {
                    recor.setVerifyId(Verify.getId());
                    recor.setRecieveStatus("1");
                    recor.setRecieveTime(new Date());
                    wxActGoldeneggsRecordService.doEdit(recor);
                    j.setSuccess(true);
                    j.setObj(recor);
                }
            } else {
                j.setSuccess(false);
                j.setObj("????????????,????????????");
            }
        } catch (Exception e) {
            j.setSuccess(false);
            j.setObj("????????????,??????????????????");
        }
        return j;
    }

    /**
     * ??????
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/doSearch", method = {RequestMethod.GET,
            RequestMethod.POST})
    @ResponseBody
    public AjaxJson doSearch(@ModelAttribute WxActGoldeneggsRecord Record,
                             HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        AjaxJson j = new AjaxJson();
        String code = request.getParameter("search");
        try {
            WxActGoldeneggsVerify veri = wxActGoldeneggsVerifyService.queryAllGoldeneggs(Record.getActId(), code);
            if (veri != null) {
                j.setObj(veri);
                j.setSuccess(true);
            } else {
                j.setSuccess(false);
            }
        } catch (Exception e) {
            j.setSuccess(false);
        }
        return j;
    }
}
