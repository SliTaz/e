package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleService;
import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.ConsumerRole;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/consumerole")
@RestController
public class ConsumerRoleController {
	@Autowired
	ConsumerRoleService consumerRoleSercice;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询应用，支持分页
	@ApiOperation(value = "Query consumerRole, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerRole>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String roleId,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		
		ConsumerRole consumeRole = new ConsumerRole();
		consumeRole.setRoleId(id);
		consumeRole.setName(name);
		consumeRole.setRemark(remark);
		
		int count = consumerRoleSercice.count(consumeRole);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerRole>>(new ArrayList<ConsumerRole>(), HttpRestStatus.NOT_FOUND);
		}

		List<ConsumerRole> list = null;// consumerRoleService.selectPage(consumeRole);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerRoleSercice.selectPage(consumeRole);
		} else {
			list = consumerRoleSercice.selectPage(consumeRole);
		}

		return new ResponseRestEntity<List<ConsumerRole>>(list, HttpRestStatus.OK, count, count);
	}
	@ApiOperation(value = "Add consumerRole", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRole(@RequestBody ConsumerRole role,BindingResult result, UriComponentsBuilder ucBuilder) {
		role.setRoleId(IDGenerate.generateCommOne(IDGenerate.CONSUMER__USR_ROLE));
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		// 是否存在相同用户名
		if (consumerRoleSercice.isRoleExist(role)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
         //新增
		consumerRoleSercice.insert(role);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, role,CommonLogImpl.CONSUMER);
         //返回处理
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/role/{id}").buildAndExpand(role.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}


	// 修改应用信息
	@ApiOperation(value = "Edit consumerRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerRole> updateRole(@PathVariable("id") String id, @Valid @RequestBody ConsumerRole consumeRole,
			BindingResult result) {

		ConsumerRole role = consumerRoleSercice.selectByPrimaryKey(id);

		if (role == null) {
			return new ResponseRestEntity<ConsumerRole>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		role.setRoleId(consumeRole.getRoleId());
		role.setCode(consumeRole.getCode());
		role.setName(consumeRole.getName());
		role.setRemark(consumeRole.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerRole>(role, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerRoleSercice.updateByPrimaryKey(role);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, role,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerRole>(role, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@ApiOperation(value = "Edit Part consumerRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerRole> updateRole(@PathVariable("id") String id, @RequestBody ConsumerRole consumeRole) {

		ConsumerRole role = consumerRoleSercice.selectByPrimaryKey(id);

		if (role == null) {
			return new ResponseRestEntity<ConsumerRole>(HttpRestStatus.NOT_FOUND);
		}
		consumeRole.setRoleId(id);
		consumerRoleSercice.updateByPrimaryKeySelective(consumeRole);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumeRole,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerRole>(role, HttpRestStatus.OK);
	}

	// 删除指定应用
	@ApiOperation(value = "Delete consumerRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerRole> deleteApp(@PathVariable("id") String id) {

		ConsumerRole role = consumerRoleSercice.selectByPrimaryKey(id);
		if (role == null) {
			return new ResponseRestEntity<ConsumerRole>(HttpRestStatus.NOT_FOUND);
		}

		consumerRoleSercice.deleteByPrimaryKey(id);
		//删除日志开始
		ConsumerRole consumer = new ConsumerRole();
		consumer.setRoleId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer,CommonLogImpl.CONSUMER);
	//删除日志结束
		return new ResponseRestEntity<ConsumerRole>(HttpRestStatus.NO_CONTENT);
	}

}
