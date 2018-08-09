package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerFamily")
@RestController
public class ConsumerFamilyController {
	@Autowired
	ConsumerFamilyService consumerFamilyService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_BUYER_F_Q')")
	@ApiOperation(value = "Query ConsumerFamily，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerFamily>> selectPage(@RequestParam(required = false) String id,
			 @RequestParam(required = false) String name,@RequestParam(required = false) String idNumber, 
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerFamily consumerFamily = new ConsumerFamily();
		// 输入idNumber查询
		if ( (id == null || "".equals(id)) && (idNumber == null || "".equals(idNumber))) {
			return new ResponseRestEntity<List<ConsumerFamily>>(new ArrayList<ConsumerFamily>(),
					HttpRestStatus.NOT_FOUND);
		}
		
		if (idNumber == null || "".equals(idNumber)) {
			consumerFamily.setFamilyId(id);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerFamily>>(new ArrayList<ConsumerFamily>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (id == null || "".equals(id)) {
					consumerFamily.setFamilyId(consumerUserClap.getFamilyId());
				} else {
					if (id.equals(consumerUserClap.getFamilyId())) {
						consumerFamily.setFamilyId(id);
					} else {
						return new ResponseRestEntity<List<ConsumerFamily>>(new ArrayList<ConsumerFamily>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		consumerFamily.setName(name);
		consumerFamily.setDeleteFlag(0);
		int count = consumerFamilyService.count(consumerFamily);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerFamily>>(new ArrayList<ConsumerFamily>(),
					HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerFamily> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerFamilyService.selectPage(consumerFamily);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerFamilyService.selectPage(consumerFamily);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerFamily>>(new ArrayList<ConsumerFamily>(),
					HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<ConsumerFamily>>(list, HttpRestStatus.OK, count, count);
	}
	@PreAuthorize("hasRole('R_BUYER_F_Q')")
	@ApiOperation(value = "Query ConsumerFamily，Support paging", notes = "")
	@RequestMapping(value = "/allFamily", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerFamily>> allFamily() {
		ConsumerFamily consumerFamily = new ConsumerFamily();
		consumerFamily.setDeleteFlag(0);
		List<ConsumerFamily> list = consumerFamilyService.selectPage(consumerFamily);
		return new ResponseRestEntity<List<ConsumerFamily>>(list, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_BUYER_F_Q')")
	@ApiOperation(value = "Query ConsumerFamily", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerFamily> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerFamily consumerFamily = consumerFamilyService.selectByPrimaryKey(id);
		if (consumerFamily == null) {
			return new ResponseRestEntity<ConsumerFamily>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerFamily>(consumerFamily, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_F_E')")
	@ApiOperation(value = "Add ConsumerFamily", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerFamily(@Valid @RequestBody ConsumerFamily consumerFamily,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		if (consumerFamilyService.isConsumerFamilyExist(consumerFamily)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
	    //consumerFamily.setFamilyId(IDGenerate.generateCommTwo(IDGenerate.CONSUMER_FAMILY));
		consumerFamily.setDeleteFlag(0);
		consumerFamilyService.insert(consumerFamily);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerFamily, CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/consumerFamily/{id}").buildAndExpand(consumerFamily.getFamilyId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,
				localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_F_E')")
	@ApiOperation(value = "Edit ConsumerFamily", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerFamily> updateConsumerFamily(@PathVariable("id") String id,
			@Valid @RequestBody ConsumerFamily consumerFamily, BindingResult result) {

		ConsumerFamily currentConsumerFamily = consumerFamilyService.selectByPrimaryKey(id);

		if (currentConsumerFamily == null) {
			return new ResponseRestEntity<ConsumerFamily>(HttpRestStatus.NOT_FOUND,
					localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerFamily.setName(consumerFamily.getName());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerFamily>(currentConsumerFamily,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerFamilyService.updateByPrimaryKey(currentConsumerFamily);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerFamily, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerFamily>(currentConsumerFamily, HttpRestStatus.OK,
				localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_F_E')")
	@ApiOperation(value = "Edit Part ConsumerFamily", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerFamily> updateConsumerFamilySelective(@PathVariable("id") String id,
			@RequestBody ConsumerFamily consumerFamily) {

		ConsumerFamily currentConsumerFamily = consumerFamilyService.selectByPrimaryKey(id);

		if (currentConsumerFamily == null) {
			return new ResponseRestEntity<ConsumerFamily>(HttpRestStatus.NOT_FOUND);
		}
		currentConsumerFamily.setFamilyId(id);
		currentConsumerFamily.setName(consumerFamily.getName());
		consumerFamilyService.updateByPrimaryKeySelective(currentConsumerFamily);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerFamily, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerFamily>(currentConsumerFamily, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_F_E')")
	@ApiOperation(value = "Delete ConsumerFamily", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerFamily> deleteConsumerFamily(@PathVariable("id") String id) {

		ConsumerFamily consumerFamily = consumerFamilyService.selectByPrimaryKey(id);
		if (consumerFamily == null) {
			return new ResponseRestEntity<ConsumerFamily>(HttpRestStatus.NOT_FOUND);
		}
		consumerFamily.setDeleteFlag(1);
		consumerFamilyService.updateByPrimaryKeySelective(consumerFamily);
		// 删除日志开始
		ConsumerFamily consumer = new ConsumerFamily();
		consumer.setFamilyId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer, CommonLogImpl.CONSUMER);
		// 删除日志结束
		return new ResponseRestEntity<ConsumerFamily>(HttpRestStatus.NO_CONTENT);
	}

}