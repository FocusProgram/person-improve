package com.jeecg.p3.commonweixin.web.back;

import com.jeecg.p3.commonweixin.def.CommonWeixinProperties;
import com.jeecg.p3.commonweixin.entity.JwSystemUserJwidVo;
import com.jeecg.p3.commonweixin.entity.JwSystemUserVo;
import com.jeecg.p3.commonweixin.entity.MyJwWebJwid;
import com.jeecg.p3.commonweixin.exception.CommonweixinException;
import com.jeecg.p3.commonweixin.service.MyJwSystemUserService;
import com.jeecg.p3.commonweixin.util.AccessTokenUtil;
import com.jeecg.p3.commonweixin.util.Constants;
import com.jeecg.p3.config.PropertiesConfig;
import com.jeecg.p3.open.entity.WeixinOpenAccount;
import com.jeecg.p3.open.service.WeixinOpenAccountService;
import com.jeecg.p3.redis.JedisPoolUtil;
import com.jeecg.p3.system.service.MyJwWebJwidService;
import com.jeecg.p3.weixin.util.FastdfsUtils;
import com.jeecg.p3.weixin.util.WxErrCodeUtil;
import com.jeecg.p3.weixinInterface.entity.WeixinAccount;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.entity.ContentType;
import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.util.SystemTools;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.StringUtils;
import org.jeecgframework.p3.core.web.BaseController;
import org.jeewx.api.core.common.WxstoreUtils;
import org.jeewx.api.third.JwThirdAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;


/**
 * ?????????</b>JwWebJwidController<br>????????????????????????
 *
 * @author pituo
 * @since???2015???12???21??? 16???33???45??? ?????????
 * @version:1.0
 */
@Controller
@RequestMapping("/commonweixin/back/myJwWebJwid")
public class MyJwWebJwidController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(MyJwWebJwidController.class);

    @Autowired
    private MyJwWebJwidService myJwWebJwidService;
    @Autowired
    private WeixinOpenAccountService weixinOpenAccountService;
    @Autowired
    private MyJwSystemUserService myJwSystemUserService;


    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "list", method = {RequestMethod.GET, RequestMethod.POST})
    public void list(@ModelAttribute MyJwWebJwid query, HttpServletResponse response, HttpServletRequest request,
                     @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                     @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String viewName = "commonweixin/back/myJwWebJwid-list.vm";
        try {
            String systemUserid = request.getSession().getAttribute("system_userid").toString();
            if (StringUtils.isEmpty(systemUserid)) {
                throw new CommonweixinException("????????????????????????");
            }
            query.setCreateBy(systemUserid);
            PageQuery<MyJwWebJwid> pageQuery = new PageQuery<MyJwWebJwid>();
            pageQuery.setPageNo(pageNo);
            pageQuery.setPageSize(pageSize);
            String jwid = request.getSession().getAttribute("jwid").toString();
            pageQuery.setQuery(query);
            velocityContext.put("jwid", jwid);
            velocityContext.put("myJwWebJwid", query);
            velocityContext.put("systemUserid", systemUserid);
            velocityContext.put("pageInfos", SystemTools.convertPaginatedList(myJwWebJwidService.queryPageList(pageQuery)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "toDetail", method = RequestMethod.GET)
    public void jwWebJwidDetail(@RequestParam(required = true, value = "id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String viewName = "commonweixin/back/myJwWebJwid-detail.vm";
        MyJwWebJwid myJwWebJwid = myJwWebJwidService.queryById(id);
        velocityContext.put("myJwWebJwid", myJwWebJwid);
        String jwid = request.getSession().getAttribute("jwid").toString();
        velocityContext.put("jwid", jwid);
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/toAdd", method = {RequestMethod.GET, RequestMethod.POST})
    public void toAddDialog(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String viewName = "commonweixin/back/myJwWebJwid-add.vm";
        String jwid = request.getSession().getAttribute("jwid").toString();
        velocityContext.put("jwid", jwid);
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "/doAdd", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson doAdd(@ModelAttribute MyJwWebJwid myJwWebJwid, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        try {
            //?????????????????????????????????
            String createBy = (String) request.getSession().getAttribute(Constants.SYSTEM_USERID);
            if (!"admin".equals(createBy)) {
                MyJwWebJwid jwWebJwid = myJwWebJwidService.queryOneByCreateBy(createBy);
                if (jwWebJwid != null) {
                    j.setSuccess(false);
                    j.setMsg("???????????????????????????????????????!");
                    return j;
                }
            }

            myJwWebJwid.setAuthType("1");
            Map<String, Object> map = AccessTokenUtil.getAccseeToken(myJwWebJwid.getWeixinAppId(), myJwWebJwid.getWeixinAppSecret());
            if (map.get("accessToken") != null) {
                myJwWebJwid.setAccessToken(map.get("accessToken").toString());
                myJwWebJwid.setTokenGetTime((Date) map.get("accessTokenTime"));
                myJwWebJwid.setApiTicket(map.get("apiTicket").toString());
                myJwWebJwid.setApiTicketTime((Date) map.get("apiTicketTime"));
                myJwWebJwid.setJsApiTicket(map.get("jsApiTicket").toString());
                myJwWebJwid.setJsApiTicketTime((Date) map.get("jsApiTicketTime"));
                j.setMsg("?????????????????????");

                WeixinAccount po = new WeixinAccount();
                po.setAccountappid(myJwWebJwid.getWeixinAppId());
                po.setAccountappsecret(myJwWebJwid.getWeixinAppSecret());
                po.setAccountaccesstoken(myJwWebJwid.getAccessToken());
                po.setAddtoekntime(myJwWebJwid.getTokenGetTime());
                po.setAccountnumber(myJwWebJwid.getWeixinNumber());
                po.setApiticket(myJwWebJwid.getApiTicket());
                po.setApiticketttime(myJwWebJwid.getApiTicketTime());
                po.setAccounttype(myJwWebJwid.getAccountType());
                po.setWeixinAccountid(myJwWebJwid.getJwid());//??????ID
                po.setJsapiticket(myJwWebJwid.getJsApiTicket());
                po.setJsapitickettime(myJwWebJwid.getJsApiTicketTime());
                try {
                    JedisPoolUtil.putWxAccount(po);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            } else {
                //update-begin--Author:zhangweijian  Date: 20181112 for??????????????????????????????
                if (map.get("errcode").equals("40164")) {
                    j.setMsg(WxErrCodeUtil.ERROR_40164 + "&nbsp;&nbsp;<a target='_blank' href='http://www.h5huodong.com/h5/detail.html?id=ff80808165e062030165e6451e6d1d58'>????????????</a>");
                } else {
                    j.setMsg("AppId??? AppSecret???????????????????????????????????? ");
                }
                //update-end--Author:zhangweijian  Date: 20181112 for??????????????????????????????
                //update-begin--Author:zhangweijian  Date: 20180910 for?????????????????????????????????
                j.setSuccess(false);
                return j;
                //update-end--Author:zhangweijian  Date: 20180910 for?????????????????????????????????
            }
            myJwWebJwid.setCreateBy(createBy);
            MyJwWebJwid myJwWebJwid2 = myJwWebJwidService.queryByJwid(myJwWebJwid.getJwid());
            if (myJwWebJwid2 != null) {
                j.setSuccess(false);
                j.setMsg("???????????????????????????!");
                return j;
            }
            myJwWebJwidService.doAdd(myJwWebJwid);
            request.getSession().setAttribute(Constants.SYSTEM_JWID, myJwWebJwid.getJwid());
            request.getSession().setAttribute(Constants.SYSTEM_JWIDNAME, myJwWebJwid.getName());
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            j.setSuccess(false);
            j.setMsg("????????????");
        }
        return j;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @RequestMapping(value = "toEdit", method = RequestMethod.GET)
    public void toEdit(@RequestParam(required = true, value = "id") String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        MyJwWebJwid myJwWebJwid = myJwWebJwidService.queryById(id);
        velocityContext.put("myJwWebJwid", myJwWebJwid);
        String viewName = "commonweixin/back/myJwWebJwid-edit.vm";
        String jwid = request.getSession().getAttribute("jwid").toString();
        velocityContext.put("jwid", jwid);
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/doEdit", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson doEdit(@ModelAttribute MyJwWebJwid myJwWebJwid, @RequestParam(required = true, value = "oldjwid") String oldjwid, HttpServletRequest request) {
        log.info("------------------------------?????????????????????---------------------------");
        AjaxJson j = new AjaxJson();
        try {
            //update-begin--Author:zhangweijian Date:20181011 for??????????????????????????????
            String authType = myJwWebJwid.getAuthType();
            if (!authType.equals("2")) {
                log.info("------------------------------AccessTokenUtil.getAccseeToken--------begin-------------------");
                Map<String, Object> map = AccessTokenUtil.getAccseeToken(myJwWebJwid.getWeixinAppId(), myJwWebJwid.getWeixinAppSecret());
                log.info("------------------------------AccessTokenUtil.getAccseeToken--------end-------------------");
                if (map.get("accessToken") != null) {
                    myJwWebJwid.setAccessToken(map.get("accessToken").toString());
                    myJwWebJwid.setTokenGetTime((Date) map.get("accessTokenTime"));
                    myJwWebJwid.setApiTicket(map.get("apiTicket").toString());
                    myJwWebJwid.setApiTicketTime((Date) map.get("apiTicketTime"));
                    myJwWebJwid.setJsApiTicket(map.get("jsApiTicket").toString());
                    myJwWebJwid.setJsApiTicketTime((Date) map.get("jsApiTicketTime"));

                    if (!oldjwid.equals(myJwWebJwid.getJwid())) {
                        //?????????????????????????????????????????????????????????ID
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myJwWebJwidService.switchDefaultOfficialAcco(myJwWebJwid.getId(), oldjwid, myJwWebJwid.getJwid());
                            }
                        });
                        t.start();

                        //5.?????????????????????ID
                        Object cache_jwid = request.getSession().getAttribute(Constants.SYSTEM_JWID);
                        if (cache_jwid != null && cache_jwid.toString().equals(oldjwid)) {
                            request.getSession().setAttribute(Constants.SYSTEM_JWID, myJwWebJwid.getJwid());
                        }
                    }

                    WeixinAccount po = new WeixinAccount();
                    po.setAccountappid(myJwWebJwid.getWeixinAppId());
                    po.setAccountappsecret(myJwWebJwid.getWeixinAppSecret());
                    po.setAccountaccesstoken(myJwWebJwid.getAccessToken());
                    po.setAddtoekntime(myJwWebJwid.getTokenGetTime());
                    po.setAccountnumber(myJwWebJwid.getWeixinNumber());
                    po.setApiticket(myJwWebJwid.getApiTicket());
                    po.setApiticketttime(myJwWebJwid.getApiTicketTime());
                    po.setAccounttype(myJwWebJwid.getAccountType());
                    po.setWeixinAccountid(myJwWebJwid.getJwid());//??????ID
                    po.setJsapiticket(myJwWebJwid.getJsApiTicket());
                    po.setJsapitickettime(myJwWebJwid.getJsApiTicketTime());
                    try {
                        log.info("--------------------JedisPoolUtil-------------setWxAccount-------------------" + myJwWebJwid.toString());
                        JedisPoolUtil.putWxAccount(po);
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                } else {
                    //update-begin--Author:zhangweijian  Date: 20181112 for??????????????????????????????
                    if (map.get("errcode").equals("40164")) {
                        j.setMsg(WxErrCodeUtil.ERROR_40164 + "&nbsp;&nbsp;<a target='_blank' href='http://www.h5huodong.com/h5/detail.html?id=ff80808165e062030165e6451e6d1d58'>????????????</a>");
                    } else {
                        j.setMsg("AppId??? AppSecret???????????????????????????????????? ");
                    }
                    //update-end--Author:zhangweijian  Date: 20181112 for??????????????????????????????
                    //update-begin--Author:zhangweijian  Date: 20180910 for?????????????????????????????????
                    j.setSuccess(false);
                    return j;
                    //update-end--Author:zhangweijian  Date: 20180910 for?????????????????????????????????
                }
            }
            myJwWebJwidService.doEdit(myJwWebJwid);
            j.setMsg("?????????????????????");
            //update-end--Author:zhangweijian Date:20181011 for??????????????????????????????
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            j.setSuccess(false);
            j.setMsg("????????????");
        }
        return j;
    }


    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    @ResponseBody
    public AjaxJson doDelete(@RequestParam(required = true, value = "id") String id) {
        AjaxJson j = new AjaxJson();
        try {
            myJwWebJwidService.doDelete(id);
            j.setMsg("????????????");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            j.setSuccess(false);
            j.setMsg("????????????");
        }
        return j;
    }

    /**
     * ?????? AccessToken
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "reset", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson resetAccessToken(@RequestParam(required = true, value = "id") String id) {
        AjaxJson json = new AjaxJson();
        try {
            log.info("------------------resetAccessToken------------------");
            String resetAccessToken = myJwWebJwidService.resetAccessToken(id);
            if (StringUtils.isNotEmpty(resetAccessToken)) {
                if ("success".equals(resetAccessToken)) {
                    json.setMsg("??????token??????");
                } else {
                    json.setSuccess(false);
                    json.setMsg("??????token?????????" + resetAccessToken);
                }
            } else {
                json.setSuccess(false);
                json.setMsg("??????token?????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            json.setSuccess(false);
            json.setMsg("??????token?????????????????????");
        }
        return json;
    }

    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "/doUpload", method = {RequestMethod.POST})
    @ResponseBody
    public AjaxJson doUpload(MultipartHttpServletRequest request, HttpServletResponse response) {
        AjaxJson j = new AjaxJson();
        try {
            MultipartFile uploadify = request.getFile("file");
        /*byte[] bytes = uploadify.getBytes();
        String realFilename=uploadify.getOriginalFilename();
        String fileExtension = realFilename.substring(realFilename.lastIndexOf("."));
        String filename=UUID.randomUUID().toString().replace("-", "")+fileExtension;
        //String uploadDir = request.getSession().getServletContext().getRealPath("upload/img/commonweixin/");
        String uploadDir = upLoadPath + "/upload/img/commonweixin/";
        File dirPath = new File(uploadDir);
        if (!dirPath.exists()) {  
            dirPath.mkdirs();  
        }  
        String sep = System.getProperty("file.separator");  
        File uploadedFile = new File(uploadDir + sep  + filename);  
        FileCopyUtils.copy(bytes, uploadedFile);*/
//      oss????????????
//		String filename = OSSBootUtil.upload(uploadify , "upload/img/commonweixin");
            Map<String, String> params = new HashMap<>();
            params.put("storePath", "1");
            String resultMap = FastdfsUtils.httpClientUploadFile(uploadify, params);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(resultMap);
            boolean result = (boolean) jsonObject.get("success");
            if (result) {
                com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) jsonObject.get("data");
                String url = (String) data.get("url");
                url = PropertiesConfig.getImageUrl() + url;
                j.setObj(url);
                j.setSuccess(true);
                j.setMsg("????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("????????????");
        }
        return j;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "toSweepCodeAuthorization", method = {RequestMethod.GET, RequestMethod.POST})
    public void toSweepCodeAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String viewName = "open/back/myJwWebJwid-sweepCodeAuthorization.vm";
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ?????????????????????
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAuthhorizationUrl")
    public AjaxJson getAuthhorizationUrl(HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        try {
            String url = CommonWeixinProperties.authhorizationUrl;
            WeixinOpenAccount weixinOpenAccount = weixinOpenAccountService.queryOneByAppid(CommonWeixinProperties.component_appid);
            if (weixinOpenAccount == null) {
                throw new CommonweixinException("??????APPID??????WEIXINOPENACCOUNT??????!");
            }
            //??????ACCESSTOKEN
            if (StringUtils.isEmpty(weixinOpenAccount.getComponentAccessToken())) {
                throw new CommonweixinException("??????????????????????????????ACCESSTOKEN");
            }
            //??????????????????
            String preAuthCode = JwThirdAPI.getPreAuthCode(CommonWeixinProperties.component_appid, weixinOpenAccount.getComponentAccessToken());
            url = url.replace("PRE_AUTH_CODE", preAuthCode);
            String redirect_uri = URLEncoder.encode(CommonWeixinProperties.authhorizationCallBackUrl + "?userId=" + request.getSession().getAttribute(Constants.SYSTEM_USERID), "UTF-8");
            url = url.replace("REDIRECT_URI", redirect_uri).replace("COMPONENT_APPID", CommonWeixinProperties.component_appid);
            log.info("===========??????????????????????????????===?????????===" + url + "============");
            j.setObj(url);
        } catch (CommonweixinException e) {
            e.printStackTrace();
            j.setMsg("??????????????????????????????!");
            j.setSuccess(false);
            log.error("getAuthhorizationUrl error={}", new Object[]{e.getMessage()});
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAuthhorizationUrl error={}", new Object[]{e});
            j.setMsg("??????????????????????????????!");
            j.setSuccess(false);
        }
        return j;
    }

    /**
     * ??????????????????
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "callback", method = {RequestMethod.GET, RequestMethod.POST})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String message = "???????????????";
        VelocityContext velocityContext = new VelocityContext();
        String viewName = "open/back/myJwWebJwid-callback.vm";
        velocityContext.put("message", message);
        try {
            String authCode = request.getParameter("auth_code");
            WeixinOpenAccount weixinOpenAccount = weixinOpenAccountService.queryOneByAppid(CommonWeixinProperties.component_appid);

            //????????????????????????ACCESSTOKEN
            String componentAccessToken = weixinOpenAccount.getComponentAccessToken();
            if (StringUtils.isEmpty(componentAccessToken)) {
                throw new CommonweixinException("??????????????????????????????ACCESSTOKEN??????!");
            }

            //????????????
            String urlFormat = CommonWeixinProperties.getApiQueryAuth.replace("COMPONENT_ACCESS_TOKEN", componentAccessToken);
            JSONObject json = new JSONObject();
            json.put("component_appid", CommonWeixinProperties.component_appid);
            json.put("authorization_code", authCode);
            log.info("??????????????????????????????????????????????????????{}", new Object[]{json.toString()});
            JSONObject jsonObject = WxstoreUtils.httpRequest(urlFormat, "POST", json.toString());
            log.info("??????????????????????????????????????????????????????{}", new Object[]{jsonObject});
            if (jsonObject != null && !jsonObject.containsKey("errcode")) {
                MyJwWebJwid myJwWebJwid = new MyJwWebJwid();
                // ????????????????????????????????????
                myJwWebJwid.setCreateBy(request.getParameter("userId"));
                save(jsonObject, myJwWebJwid);
                // ???????????????token?????????????????????
                String getAuthorizerInfoUrl = CommonWeixinProperties.getAuthorizerInfo.replace("COMPONENT_ACCESS_TOKEN", componentAccessToken);
                JSONObject j = new JSONObject();
                // ???????????????appid
                j.put("component_appid", CommonWeixinProperties.component_appid);
                // ???????????????appid
                j.put("authorizer_appid", myJwWebJwid.getWeixinAppId());
                JSONObject jsonObj = WxstoreUtils.httpRequest(getAuthorizerInfoUrl, "POST", j.toString());
                log.info("===========??????????????????===???????????????????????????Info===" + jsonObj.toString() + "===========");
                if (jsonObj != null && !jsonObj.containsKey("errcode")) {
                    // ??????????????????????????????????????????????????????????????????
                    callbackUpdate(jsonObj, myJwWebJwid);
                }
            }
        } catch (CommonweixinException e) {
            e.printStackTrace();
            message = "????????????";
            log.error("?????????????????????????????????????????????????????????={}", new Object[]{e.getMessage()});

        } catch (Exception e) {
            e.printStackTrace();
            log.error("?????????????????????????????????????????????????????????={}", new Object[]{e});
            message = "????????????";
        }
	/*PrintWriter pw = null;
	try {
		//response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		pw = response.getWriter();
		pw.write("<h2 style='text-align:center;color:#FEA128;'>"+message+"</h2>");
		pw.write("<h3 style='text-align:center;color:#FEA128;'>???????????????????????????</h3>");
		pw.flush();
	} finally{
		pw.close();
	}*/
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * ????????????
     *
     * @param jsonObj
     * @param myJwWebJwid
     */
    private void callbackUpdate(JSONObject jsonObj, MyJwWebJwid myJwWebJwid) {
        try {
            String authorizerInfoStr = jsonObj.getString("authorizer_info");
            String qrcodeUrl = null;
            JSONObject authorizerInfoJson = JSONObject.fromObject(authorizerInfoStr);
            if (authorizerInfoJson.containsKey("qrcode_url")) {
                qrcodeUrl = authorizerInfoJson.getString("qrcode_url");
            }
            String nickName = authorizerInfoJson.getString("nick_name");
            String headImg = null;
            if (authorizerInfoJson.containsKey("head_img") && StringUtils.isNotEmpty(authorizerInfoJson.getString("head_img"))) {
                headImg = authorizerInfoJson.getString("head_img");
                myJwWebJwid.setHeadimgurl(headImg);
            }
            String serviceTypeInfo = authorizerInfoJson.getString("service_type_info");
            String verifyTypeInfo = authorizerInfoJson.getString("verify_type_info");
            String userName = authorizerInfoJson.getString("user_name");
            String businessInfo = authorizerInfoJson.getString("business_info");
            String alias = "";
            if (authorizerInfoJson.containsKey("alias")) {
                alias = authorizerInfoJson.getString("alias");
            }
            String authorizationInfoS = jsonObj.getString("authorization_info");
            JSONObject authorization_info_json = JSONObject.fromObject(authorizationInfoS);
            String func_info = authorization_info_json.getString("func_info");
            myJwWebJwid.setWeixinNumber(alias);
            myJwWebJwid.setBusinessInfo(businessInfo);
            myJwWebJwid.setFuncInfo(func_info);
            myJwWebJwid.setName(nickName);
            String fileName = UUID.randomUUID().toString().replace("-", "").toUpperCase() + ".jpg";
            String uploadDir = "upload/img/commonweixin";
            //update-begin--Author:zhaofei  Date: 20191016 for??????????????????????????????????????????????????????
//		MultipartFile multipartFile = createFileItem(qrcodeUrl,fileName);
//		String fileNames = OSSBootUtil.upload(multipartFile , uploadDir);
            //update-end--Author:zhaofei  Date: 20191016 for??????????????????????????????????????????????????????
//		download(qrcodeUrl, fileName, uploadDir);
            myJwWebJwid.setQrcodeimg(qrcodeUrl);
            JSONObject json = JSONObject.fromObject(serviceTypeInfo);
            if (json != null && json.containsKey("id")) {
                int accountType = json.getInt("id");
                if (2 == accountType) {
                    myJwWebJwid.setAccountType("1");
                } else {
                    myJwWebJwid.setAccountType("2");
                }
            }
            json = JSONObject.fromObject(verifyTypeInfo);
            if (json != null && json.containsKey("id")) {
                int authStatus = json.getInt("id");
                if (authStatus == -1) {
                    myJwWebJwid.setAuthStatus("0");
                } else {
                    myJwWebJwid.setAuthStatus("1");
                }
            }
            myJwWebJwid.setJwid(userName);
            //??????apiticket
            Map<String, String> apiTicket = AccessTokenUtil.getApiTicket(myJwWebJwid.getAccessToken());
            if ("true".equals(apiTicket.get("status"))) {
                myJwWebJwid.setApiTicket(apiTicket.get("apiTicket"));
                myJwWebJwid.setApiTicketTime(new Date());
                myJwWebJwid.setJsApiTicket(apiTicket.get("jsApiTicket"));
                myJwWebJwid.setJsApiTicketTime(new Date());
            }
            //TODO ??????????????????????????????????????????
            MyJwWebJwid webJwid = myJwWebJwidService.queryByJwid(userName);
            if (webJwid == null) {
                myJwWebJwidService.doAdd(myJwWebJwid);
            } else {
                myJwWebJwid.setId(webJwid.getId());
                myJwWebJwidService.doUpdate(myJwWebJwid);
            }
            //-------H5??????????????????????????????redis??????,???token??????redis-------------------------------------------
            try {
                log.info("----------???????????????H5??????????????????????????????redis??????token??????-------------");
                WeixinAccount po = new WeixinAccount();
                po.setAccountappid(myJwWebJwid.getWeixinAppId());
                po.setAccountappsecret(myJwWebJwid.getWeixinAppSecret());
                po.setAccountaccesstoken(myJwWebJwid.getAccessToken());
                po.setAddtoekntime(myJwWebJwid.getTokenGetTime());
                po.setAccountnumber(myJwWebJwid.getWeixinNumber());
                po.setApiticket(myJwWebJwid.getApiTicket());
                po.setApiticketttime(myJwWebJwid.getApiTicketTime());
                po.setAccounttype(myJwWebJwid.getAccountType());
                po.setWeixinAccountid(myJwWebJwid.getJwid());//??????ID
                po.setJsapiticket(myJwWebJwid.getJsApiTicket());
                po.setJsapitickettime(myJwWebJwid.getJsApiTicketTime());
                JedisPoolUtil.putWxAccount(po);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("----------???????????????H5??????????????????????????????redis??????token??????-------------" + e.toString());
            }
            //--------H5??????????????????????????????redis??????---------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonweixinException("??????????????????==UPDATE???????????????" + e.getMessage());
        }
    }

    /**
     * ????????????
     *
     * @param jsonObject
     * @param myJwWebJwid
     */
    private void save(JSONObject jsonObject, MyJwWebJwid myJwWebJwid) {
        try {
            String authorizationInfoStr = jsonObject.getString("authorization_info");
            JSONObject authorizationInfoJson = JSONObject.fromObject(authorizationInfoStr);
            String authorizerAppid = null;
            if (authorizationInfoJson.containsKey("authorizer_appid")) {
                authorizerAppid = authorizationInfoJson.getString("authorizer_appid");
            } else if (jsonObject.containsKey("authorizer_appid")) {
                authorizerAppid = jsonObject.getString("authorizer_appid");
            }
            String authorizerAccessToken = authorizationInfoJson.getString("authorizer_access_token");
            String authorizerRefreshToken = authorizationInfoJson.getString("authorizer_refresh_token");
            String funcInfoStr = "";
            if (authorizationInfoJson.containsKey("func_info")) {
                funcInfoStr = authorizationInfoJson.getString("func_info");
            } else if (jsonObject.containsKey("func_info")) {
                funcInfoStr = jsonObject.getString("func_info");
            }
            myJwWebJwid.setAuthorizationInfo(authorizationInfoStr);
            myJwWebJwid.setAccessToken(authorizerAccessToken);
            myJwWebJwid.setTokenGetTime(new Date());
            myJwWebJwid.setWeixinAppId(authorizerAppid);
            myJwWebJwid.setAuthorizerRefreshToken(authorizerRefreshToken);
            myJwWebJwid.setFuncInfo(funcInfoStr);
            myJwWebJwid.setAuthType("2");
            //???????????????1?????????2????????????
            myJwWebJwid.setAuthorizationStatus("1");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonweixinException("??????????????????==DOADD???????????????" + e.getMessage());
        }

    }

    /**
     * @param urlString
     * @param filename
     * @param savePath
     * @throws IOException
     */
    private void download(String urlString, String filename, String savePath) throws IOException {
        OutputStream os = null;
        InputStream is = null;
        try {
            log.info("??????????????????????????????????????????{},?????????????????????{},???????????????{}", new Object[]{urlString, filename, savePath});
            // ??????URL
            URL url = new URL(urlString);
            // ????????????
            URLConnection con = url.openConnection();
            // ?????????
            is = con.getInputStream();
            // 1K???????????????
            byte[] bs = new byte[1024];
            // ????????????????????????
            int len;
            // ??????????????????
            String sep = System.getProperty("file.separator");
            os = new FileOutputStream(savePath + sep + filename);
            // ????????????
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("============???????????????????????????============,error={}", e);
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

//update-begin-zhangweijian-----Date:20180808---for:?????????????????????ID

    /**
     * @param response
     * @param request
     * @throws Exception
     * @????????????????????????????????????ID??????
     * @author zhangweijian
     */
    @RequestMapping(value = "toSwitchDefaultOfficialAcco", method = RequestMethod.GET)
    public void toSwitchDefaultOfficialAcco(@RequestParam String jwid, HttpServletResponse response, HttpServletRequest request) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("jwid", jwid);
        String viewName = "commonweixin/back/switchDefaultOfficialAcco.vm";
        ViewVelocity.view(request, response, viewName, velocityContext);
    }

    /**
     * @param jwid
     * @return
     * @??????:?????????????????????ID
     * @??????:liwenhui
     * @??????:2018-3-15 ??????01:59:14
     * @?????????
     */
    @RequestMapping(value = "switchDefaultOfficialAcco", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson switchDefaultOfficialAcco(@RequestParam final String jwid, @RequestParam final String newJwid) {
        AjaxJson j = new AjaxJson();
        try {
            MyJwWebJwid oldJwid = myJwWebJwidService.queryByJwid(newJwid);
            if (oldJwid != null) {
                j.setMsg("??????????????????ID?????????");
                j.setSuccess(false);
                return j;
            }
            //?????????????????????????????????
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    myJwWebJwidService.switchDefaultOfficialAcco(oldJwid.getId(), jwid, newJwid);
                }
            });
            t.start();
            j.setMsg("?????????????????????ID?????????,???????????????");
            j.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            j.setMsg("????????????");
            j.setSuccess(false);
        }
        return j;
    }
//update-end-zhangweijian-----Date:20180808---for:?????????????????????ID

    //update-begin--Author:zhangweijian Date:20181019 for???????????????????????????

    /**
     * @?????????????????????????????????
     */
    @RequestMapping(value = "searchManager", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson searchManager(@RequestParam String phone) {
        AjaxJson j = new AjaxJson();
        try {
            List<JwSystemUserVo> jwSystemUser = myJwSystemUserService.queryByPhone(phone);
            if (jwSystemUser.size() > 0) {
                j.setObj(jwSystemUser.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    /**
     * @param userId
     * @return
     * @??????????????????????????????
     */
    @RequestMapping(value = "authManager", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson authManager(@RequestParam String userId, @RequestParam String jwid, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        try {
            JwSystemUserJwidVo jwSystemUserJwid = new JwSystemUserJwidVo();
            jwSystemUserJwid.setUserId(userId);
            jwSystemUserJwid.setJwid(jwid);
            myJwSystemUserService.authManager(jwSystemUserJwid);
            j.setSuccess(true);
            j.setMsg("???????????????");
            j.setObj(jwSystemUserJwid.getId());
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("????????????????????????");
        }
        return j;
    }

    /**
     * @param request
     * @return
     * @???????????????????????????????????????
     */
    @RequestMapping(value = "getManager", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson getManager(HttpServletRequest request, @RequestParam String jwid) {
        AjaxJson j = new AjaxJson();
        try {
            List<JwSystemUserJwidVo> jwSystemUserJwid = myJwSystemUserService.queryByJwid(jwid);
            if (jwSystemUserJwid.size() > 0) {
                j.setObj(jwSystemUserJwid);
            } else {
                j.setObj("");
            }
            j.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    /**
     * @?????????????????????
     */
    @RequestMapping(value = "cancelAuth", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public AjaxJson cancelAuth(HttpServletRequest request, @RequestParam String id) {
        AjaxJson j = new AjaxJson();
        try {
            myJwSystemUserService.deleteById(id);
            j.setSuccess(true);
            j.setMsg("????????????");
        } catch (Exception e) {
            e.printStackTrace();
            j.setMsg("????????????");
        }
        return j;
    }
    //update-end--Author:zhangweijian Date:20181019 for???????????????????????????

    /**
     * url????????? MultipartFile??????
     *
     * @param url
     * @param fileName
     * @return
     * @throws Exception
     */
    private static MultipartFile createFileItem(String url, String fileName) throws Exception {
        FileItem item = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            //????????????????????????????????????????????????
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();

                FileItemFactory factory = new DiskFileItemFactory(16, null);
                String textFieldName = "uploadfile";
                item = factory.createItem(textFieldName, ContentType.APPLICATION_OCTET_STREAM.toString(), false, fileName);
                OutputStream os = item.getOutputStream();

                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("??????????????????", e);
        }

        return new CommonsMultipartFile(item);
    }
}
