package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerGroupUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/consumerGroupUser")
@RestController
public class ConsumerGroupUserController {
	
	@Autowired
	ConsumerGroupUserService consumerGroupUserService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Autowired
	ConsumerUserService consumerUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_BUYER_G_Q')")
	@ApiOperation(value = "Query consumerGroup, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerGroupUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String userId, 
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerGroupUserKey consumerGroupUserKey = new ConsumerGroupUserKey();
		if ((idNumber == null || "".equals(idNumber))&& (userId == null || "".equals(userId))) {
		return new ResponseRestEntity<List<ConsumerGroupUserKey>>(new ArrayList<ConsumerGroupUserKey>(),
				HttpRestStatus.NOT_FOUND);
	}
		//输入idNumber查询
		if (idNumber == null || "".equals(idNumber)) {
			consumerGroupUserKey.setUserId(userId);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerGroupUserKey>>(new ArrayList<ConsumerGroupUserKey>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (userId == null || "".equals(userId)) {
					consumerGroupUserKey.setUserId(consumerUserClap.getUserId());
				} else {
					if (userId.equals(consumerUserClap.getUserId())) {
						consumerGroupUserKey.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<ConsumerGroupUserKey>>(new ArrayList<ConsumerGroupUserKey>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		consumerGroupUserKey.setConsumerGroupId(id);
		
		
		int count = consumerGroupUserService.count(consumerGroupUserKey);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerGroupUserKey>>(new ArrayList<ConsumerGroupUserKey>(), HttpRestStatus.NOT_FOUND);
		}

		List<ConsumerGroupUserKey> list = null;// consumerRoleService.selectPage(consumeRole);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerGroupUserService.selectPage(consumerGroupUserKey);
		} else {
			list = consumerGroupUserService.selectPage(consumerGroupUserKey);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerGroupUserKey>>(new ArrayList<ConsumerGroupUserKey>(),HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerGroupUserKey> listNew = new ArrayList<ConsumerGroupUserKey>();
		for(ConsumerGroupUserKey bean:list){
			 ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(bean.getUserId());
			if(consumerUserClap!=null){
				bean.setIdNumber(consumerUserClap.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerGroupUserKey>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_BUYER_G_E')")
	@ApiOperation(value = "Add consumerGroup", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@RequestBody ConsumerGroupUserKey consumerGroupUserKey,BindingResult result, UriComponentsBuilder ucBuilder) {
		//consumerGroup.setConsumerGroupId(System.currentTimeMillis() + "");
	/*	consumerGroup.setConsumerGroupId(IDGenerate.generateCONSUMER_USER_ID());*/
		// 校验

		consumerGroupUserKey.setIdNumber(CommonFun.getRelVid(consumerGroupUserKey.getIdNumber()));
		if("userId".equals(consumerGroupUserKey.getUserId())){
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(consumerGroupUserKey.getIdNumber());
		if (consumerUserClap != null) {
			consumerGroupUserKey.setUserId(consumerUserClap.getUserId());
		}
		else{
			return new ResponseRestEntity<Void>(HttpRestStatus.CONSUMER_NOT_FOUND,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
	}
		else{
			ConsumerUser comsumerUser=consumerUserService.selectByPrimaryKey(consumerGroupUserKey.getUserId());
			if (comsumerUser != null) {
				consumerGroupUserKey.setUserId(comsumerUser.getUserId());
			}
			else{
				return new ResponseRestEntity<Void>(HttpRestStatus.USERID_NOT_FOUND,localeMessageSourceService.getMessage("common.create.conflict.message"));
			}
		}
/*		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(consumerGroupUserKey.getIdNumber());
		if (consumerUserClap==null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.bank.message"));
		}
		consumerGroupUserKey.setUserId(consumerUserClap.getUserId());*/
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				List<ConsumerGroupUserKey> list = consumerGroupUserService.selectPage(consumerGroupUserKey);
				if (list!=null&&list.size()>0) {
					return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
				}
         //新增
		consumerGroupUserService.insert(consumerGroupUserKey);
         //返回处理
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerGroupUser/{id}").buildAndExpand(consumerGroupUserKey.getConsumerGroupId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}


	// 修改应用信息
	/*@ApiOperation(value = "Edit consumerGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerGroupUserKey> updateConsumerGroup(@PathVariable("id") String id,@RequestBody ConsumerGroup consumerGroup) {

		ConsumerGroup currenConsumerGroup = consumerGroupUserService.selectByPrimaryKey(id);

		if (currenConsumerGroup == null) {
			return new ResponseRestEntity<ConsumerGroupUserKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currenConsumerGroup.setName(consumerGroup.getName());
		currenConsumerGroup.setStatus(consumerGroup.getStatus());
		currenConsumerGroup.setRemark(consumerGroup.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerGroup>(role, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerGroupUserService.updateByPrimaryKey(currenConsumerGroup);

		return new ResponseRestEntity<ConsumerGroupUserKey>(currenConsumerGroup, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}*/

	// 修改部分应用信息
	/*@ApiOperation(value = "Edit Part consumerGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerGroupUserKey> updateConsumerGroupSelective(@PathVariable("id") String id, @RequestBody ConsumerGroup consumerGroup) {

		ConsumerGroup currenConsumerGroup = consumerGroupUserService.selectByPrimaryKey(id);

		if (currenConsumerGroup == null) {
			return new ResponseRestEntity<ConsumerGroupUserKey>(HttpRestStatus.NOT_FOUND);
		}
		consumerGroup.setConsumerGroupId(id);
		consumerGroupUserService.updateByPrimaryKeySelective(consumerGroup);

		return new ResponseRestEntity<ConsumerGroupUserKey>(currenConsumerGroup, HttpRestStatus.OK);
	}*/

	// 删除指定应用
	/*@ApiOperation(value = "Delete consumerGroup", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerGroupUserKey> deleteConsumerGroup(@PathVariable("id") String id) {

		ConsumerGroup consumerGroup = consumerGroupUserService.selectByPrimaryKey(id);
		if (consumerGroup == null) {
			return new ResponseRestEntity<ConsumerGroupUserKey>(HttpRestStatus.NOT_FOUND);
		}
		
		consumerGroupUserService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<ConsumerGroupUserKey>(HttpRestStatus.NO_CONTENT);
	}*/

	
	//删除组用户
	@PreAuthorize("hasRole('R_BUYER_G_E')")
	@ApiOperation(value = "deleteConsumerGroupUser", notes = "")
	@RequestMapping(value = "/deleteConsumerGroupUser", method = RequestMethod.GET)
	public ResponseRestEntity<Void> deleteConsumerGroupUser(@RequestParam(required = false) String id,@RequestParam(required = false) String userId) {
		ConsumerGroupUserKey consumerGroupUserKey = new ConsumerGroupUserKey();
		consumerGroupUserKey.setConsumerGroupId(id);
		consumerGroupUserKey.setUserId(userId);
		
		consumerGroupUserService.deleteConsumerGroupUser(consumerGroupUserKey);
		
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}
	
	
}
