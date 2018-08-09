package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerUserClap")
@RestController
public class ConsumerUserClapController {

	private static final Logger log = LoggerFactory.getLogger(ConsumerUserClapController.class);

	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Autowired
	MerchantUserService merchantUserService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	@Value("${url.clap.get_clap}")
	private String URL_CLAP_GET_CLAP;

	// 查询Clap卡，支持分页
	@PreAuthorize("hasRole('R_BUYER_P_C_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query ConsumerUserClap，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUserClap>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String name1, @RequestParam(required = false) String name2, @RequestParam(required = false) String lastName1,
			@RequestParam(required = false) String lastName2, @RequestParam(required = false) Integer sex, @RequestParam(required = false) String communityCode, @RequestParam(required = false) String familyId,
			@RequestParam(required = false) String clapNo, @RequestParam(required = false) String clapSeqNo, @RequestParam(required = false) String clapStoreNo, @RequestParam(required = false) String sellerUserId,
			@RequestParam(required = false) String sellerIdNumber, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerUserClap consumerUserClap = new ConsumerUserClap();
		// 必须输入一个进行查询
		if ((userId == null || "".equals(userId)) && (id == null || "".equals(id)) && (idNumber == null || "".equals(idNumber)) && (communityCode == null || "".equals(communityCode))
				&& (familyId == null || "".equals(familyId)) && (familyId == null || "".equals(clapSeqNo)) && (clapNo == null || "".equals(clapNo)) && (sellerUserId == null || "".equals(sellerUserId))
				&& (sellerIdNumber == null || "".equals(sellerIdNumber))) {
			return new ResponseRestEntity<List<ConsumerUserClap>>(new ArrayList<ConsumerUserClap>(), HttpRestStatus.NOT_FOUND);
		}

		if ((userId == null || "".equals(userId)) && (idNumber == null || "".equals(idNumber))) {
			if (sellerUserId != null && !"".equals(sellerUserId)) {
				MerchantUser merchantUserSerResult = merchantUserService.selectByPrimaryKey(sellerUserId);
				clapStoreNo = merchantUserSerResult.getClapStoreNo();
			} else if (sellerIdNumber != null && !"".equals(sellerIdNumber)) {
				MerchantUser merchantUserSerResult = merchantUserService.selectByIdNumber(sellerIdNumber);
				clapStoreNo = merchantUserSerResult.getClapStoreNo();
			}

		}

		consumerUserClap.setConsumerUserClapId(id);
		consumerUserClap.setUserId(userId);
		consumerUserClap.setIdNumber(idNumber);
		consumerUserClap.setStatus(status);
		consumerUserClap.setName1(name1);
		consumerUserClap.setName2(name2);
		consumerUserClap.setLastName1(lastName1);
		consumerUserClap.setLastName2(lastName2);
		consumerUserClap.setSex(sex);
		consumerUserClap.setCommunityCode(communityCode);
		consumerUserClap.setFamilyId(familyId);
		consumerUserClap.setClapSeqNo(clapSeqNo);
		consumerUserClap.setClapStoreNo(clapStoreNo);
		consumerUserClap.setClapNo(clapNo);

		List<ConsumerUserClap> list = new ArrayList<ConsumerUserClap>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserClapService.selectPage(consumerUserClap);

		} else {
			list = consumerUserClapService.selectPage(consumerUserClap);
		}

		int count = consumerUserClapService.count(consumerUserClap);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUserClap>>(new ArrayList<ConsumerUserClap>(), HttpRestStatus.NOT_FOUND);
		}

		for (ConsumerUserClap consumerUserClapTmp : list) {
			String tmpName = "";
			if (consumerUserClapTmp.getName1() != null) {
				tmpName += consumerUserClapTmp.getName1();
			}
			if (consumerUserClapTmp.getLastName1() != null) {
				tmpName += " " + consumerUserClapTmp.getLastName1();
			}
			if ("".equals(tmpName)) {
				if (consumerUserClapTmp.getName2() != null) {
					tmpName += consumerUserClapTmp.getName1();
				}
				if (consumerUserClapTmp.getLastName2() != null) {
					tmpName += " " + consumerUserClapTmp.getLastName1();
				}
			}
			consumerUserClapTmp.setRelUserName(tmpName);

			MerchantUser merchantUserResult = merchantUserService.selectByClapId(consumerUserClapTmp.getClapStoreNo());
			if (merchantUserResult != null) {
				if (merchantUserResult.getIdNumber() != null) {
					consumerUserClapTmp.setSellerIdNumber(merchantUserResult.getIdNumber());
				}
				if (merchantUserResult.getUserId() != null) {
					consumerUserClapTmp.setSellerId(merchantUserResult.getUserId());
				}
			}

		}

		return new ResponseRestEntity<List<ConsumerUserClap>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询consumerfamilyUser
	@PreAuthorize("hasRole('R_BUYER_P_C_Q')")
	@ApiOperation(value = "Query consumerfamilyUser", notes = "")
	@RequestMapping(value = "/{familyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUserClap> selectByFamilyId(@PathVariable("familyId") String familyId) {
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByFamilyId(familyId);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerUserClap>(consumerUserClap, HttpRestStatus.OK);
	}

	// 查询Clap卡
	@PreAuthorize("hasRole('R_BUYER_P_C_Q')")
	@ApiOperation(value = "Query ConsumerUserClap", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUserClap> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByPrimaryKey(id);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerUserClap>(consumerUserClap, HttpRestStatus.OK);
	}

	// 新增Clap卡
	@PreAuthorize("hasRole('R_BUYER_P_C_E')")
	@ApiOperation(value = "Add ConsumerUserClap", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody ConsumerUserClap consumerUserClap, BindingResult result, UriComponentsBuilder ucBuilder) {

		consumerUserClap.setConsumerUserClapId(IDGenerate.generateCONSUMER_USER_ID());
		consumerUserClap.setBindTime(PageHelperUtil.getCurrentDate());
		consumerUserClap.setIdNumber(CommonFun.getRelVid(consumerUserClap.getIdNumber()));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		List<ConsumerUserClap> consumerUserClapList = consumerUserClapService.selectByUserId(consumerUserClap.getUserId());
		if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.user.message"));
		}

		consumerUserClapService.insert(consumerUserClap);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerUserClap, CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerUserClap/{id}").buildAndExpand(consumerUserClap.getConsumerUserClapId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改Clap卡信息
	@PreAuthorize("hasRole('R_BUYER_P_C_E')")
	@ApiOperation(value = "Edit ConsumerUserClap", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUserClap> update(@PathVariable("id") String id, @Valid @RequestBody ConsumerUserClap consumerUserClap, BindingResult result) {

		// consumerUserClap.setUserId(CommonFun.getRelVid(consumerUserClap.getUserId()));
		ConsumerUserClap currentConsumerUserClap = consumerUserClapService.selectByPrimaryKey(id);

		if (currentConsumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerUserClap.setUserId(consumerUserClap.getUserId());
		currentConsumerUserClap.setIdNumber(CommonFun.getRelVid(consumerUserClap.getIdNumber()));
		currentConsumerUserClap.setStatus(consumerUserClap.getStatus());
		currentConsumerUserClap.setName1(consumerUserClap.getName1());
		currentConsumerUserClap.setName2(consumerUserClap.getName2());
		currentConsumerUserClap.setLastName1(consumerUserClap.getLastName1());
		currentConsumerUserClap.setLastName2(consumerUserClap.getLastName2());
		currentConsumerUserClap.setSex(consumerUserClap.getSex());
		currentConsumerUserClap.setDatebirth(consumerUserClap.getDatebirth());
		currentConsumerUserClap.setCommunityCode(consumerUserClap.getCommunityCode());
		currentConsumerUserClap.setFamilyId(consumerUserClap.getFamilyId());
		currentConsumerUserClap.setClapSeqNo(consumerUserClap.getClapSeqNo());
		currentConsumerUserClap.setClapNo(consumerUserClap.getClapNo());
		currentConsumerUserClap.setClapStoreNo(consumerUserClap.getClapStoreNo());
		currentConsumerUserClap.setBindTime(consumerUserClap.getBindTime());
		currentConsumerUserClap.setRemark(consumerUserClap.getRemark());
		currentConsumerUserClap.setCommunityName(consumerUserClap.getCommunityName());
		currentConsumerUserClap.setClapStoreName(consumerUserClap.getClapStoreName());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<ConsumerUserClap>(currentConsumerUserClap, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerUserClapService.updateByPrimaryKey(currentConsumerUserClap);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerUserClap, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserClap>(currentConsumerUserClap, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分Clap卡信息
	@PreAuthorize("hasRole('R_BUYER_P_C_E')")
	@ApiOperation(value = "Edit Part ConsumerUserClap", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerUserClap> updateSelective(@PathVariable("id") String id, @RequestBody ConsumerUserClap consumerUserClap) {

		ConsumerUserClap currentConsumerUserClap = consumerUserClapService.selectByPrimaryKey(id);

		if (currentConsumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.NOT_FOUND);
		}
		consumerUserClap.setConsumerUserClapId(id);
		currentConsumerUserClap.setUserId(consumerUserClap.getUserId());
		currentConsumerUserClap.setFamilyId(consumerUserClap.getFamilyId());
		currentConsumerUserClap.setClapNo(consumerUserClap.getClapNo());
		currentConsumerUserClap.setBindTime(consumerUserClap.getBindTime());
		currentConsumerUserClap.setRemark(consumerUserClap.getRemark());
		consumerUserClapService.updateByPrimaryKeySelective(consumerUserClap);// ?
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerUserClap, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserClap>(currentConsumerUserClap, HttpRestStatus.OK);
	}

	// 删除指定Clap卡
	@PreAuthorize("hasRole('R_BUYER_P_C_E') or hasRole('CONSUMER')")
	@ApiOperation(value = "Delete ConsumerUserClap", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerUserClap> delete(@PathVariable("id") String id) {

		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByPrimaryKey(id);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.NOT_FOUND);
		}

		consumerUserClapService.deleteByPrimaryKey(id);
		// 删除日志开始
		ConsumerUserClap consumer = new ConsumerUserClap();
		consumer.setConsumerUserClapId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer, CommonLogImpl.CONSUMER);
		// 删除日志结束
		return new ResponseRestEntity<ConsumerUserClap>(HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_P_C_E') or hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/synchronizeConsumerClap", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUserClap> synchronizeConsumerClap(@RequestParam String userId) {
		if (StringUtils.isEmpty(userId)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid  not empty");
		}
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(userId);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		} else {
			if (consumerUserClap.getBindTime() != null) {
				int CLAP_SYN_LIMIT_DAY = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.CLAP_SYN_LIMIT_DAY);
				if ((new Date().getTime() - consumerUserClap.getBindTime().getTime()) < (1000 * 3600 * 24 * CLAP_SYN_LIMIT_DAY)) {
//					/return new ResponseRestEntity<>(HttpRestStatus.CLAP_SYN_TIME, "The number of synchronization at a time");
				}
			}
		}
		try {
			return consumerUserClapService.synchronizeConsumerClap(consumerUserClap, userId, httpClient, config, URL_CLAP_GET_CLAP);
		} catch (Exception e) {
			return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_FAILD, "get patriot exception");
		}
	}
}