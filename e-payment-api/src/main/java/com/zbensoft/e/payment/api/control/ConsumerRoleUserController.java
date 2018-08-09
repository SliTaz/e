package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumeRoleUser")
@RestController
public class ConsumerRoleUserController {
	@Autowired
	ConsumerRoleUserService consumerRoleUserService;
	
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询用户应用，支持分页
	@ApiOperation(value = "Query ConsumerRoleUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerRoleUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ConsumerRoleUserKey consumeRoleUserKey = new ConsumerRoleUserKey();
		consumeRoleUserKey.setRoleId(id);
		consumeRoleUserKey.setUserId(userId);

		int count = consumerRoleUserService.count(consumeRoleUserKey);
		List<ConsumerRoleUserKey> list = consumerRoleUserService.selectPage(consumeRoleUserKey);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerRoleUserKey>>(new ArrayList<ConsumerRoleUserKey>(),
					HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ConsumerRoleUserKey>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户应用
	@ApiOperation(value = "Query ConsumerRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerRoleUserKey> selectByPrimaryKey(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		ConsumerRoleUserKey consumeRoleUserKey = new ConsumerRoleUserKey();
		consumeRoleUserKey.setRoleId(roleId);
		consumeRoleUserKey.setUserId(userId);
		ConsumerRoleUserKey consumeRoleUser = consumerRoleUserService.selectByPrimaryKey(consumeRoleUserKey);
		if (consumeRoleUser == null) {
			return new ResponseRestEntity<ConsumerRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerRoleUserKey>(consumeRoleUser, HttpRestStatus.OK);
	}
	
	//通过userId查询
	@ApiOperation(value = "Query ConsumerRoleUser by userId", notes = "")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<ConsumerRoleUserKey>> selectByUserId(@PathVariable("userId") String userId) {
		List<ConsumerRoleUserKey> list = consumerRoleUserService.selectByUserId(userId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerRoleUserKey>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ConsumerRoleUserKey>>(list, HttpRestStatus.OK);
	}

	// 新增用户应用
	@ApiOperation(value = "Add user application", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumeRoleUser(@RequestBody ConsumerRoleUserKey consumeRoleUser,
			UriComponentsBuilder ucBuilder) {
		consumeRoleUser.setRoleId(System.currentTimeMillis() + "");
		consumerRoleUserService.insert(consumeRoleUser);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/consumeRoleUser/{id}").buildAndExpand(consumeRoleUser.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户应用信息
	@ApiOperation(value = "Modify user application information", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerRoleUserKey> updateApp(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody ConsumerRoleUserKey consumeRoleUser) {
		ConsumerRoleUserKey consumeRoleUserKey = new ConsumerRoleUserKey();
		consumeRoleUserKey.setRoleId(roleId);
		consumeRoleUserKey.setUserId(userId);
		ConsumerRoleUserKey currentConsumeRoleUser = consumerRoleUserService.selectByPrimaryKey(consumeRoleUserKey);

		if (currentConsumeRoleUser == null) {
			return new ResponseRestEntity<ConsumerRoleUserKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumeRoleUser.setRoleId(consumeRoleUser.getRoleId());
		currentConsumeRoleUser.setUserId(consumeRoleUser.getUserId());
		consumerRoleUserService.updateByPrimaryKey(currentConsumeRoleUser);

		return new ResponseRestEntity<ConsumerRoleUserKey>(currentConsumeRoleUser, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户应用信息
	@ApiOperation(value = "Modify part of the user application information", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerRoleUserKey> updateAppSelective(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody ConsumerRoleUserKey consumeRoleUser) {
		ConsumerRoleUserKey consumeRoleUserKey = new ConsumerRoleUserKey();
		consumeRoleUserKey.setRoleId(roleId);
		consumeRoleUserKey.setUserId(userId);
		ConsumerRoleUserKey currentConsumeRoleUser = consumerRoleUserService.selectByPrimaryKey(consumeRoleUserKey);

		if (currentConsumeRoleUser == null) {
			return new ResponseRestEntity<ConsumerRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		consumeRoleUser.setRoleId(roleId);
		consumeRoleUser.setUserId(userId);
		consumerRoleUserService.updateByPrimaryKeySelective(consumeRoleUser);

		return new ResponseRestEntity<ConsumerRoleUserKey>(currentConsumeRoleUser, HttpRestStatus.OK);
	}

	// 删除指定用户应用
	@ApiOperation(value = "Delete the specified user application", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerRoleUserKey> deleteConsumeRoleUser(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		ConsumerRoleUserKey consumeRoleUserKey = new ConsumerRoleUserKey();
		consumeRoleUserKey.setRoleId(roleId);
		consumeRoleUserKey.setUserId(userId);
		ConsumerRoleUserKey consumeRoleUser = consumerRoleUserService.selectByPrimaryKey(consumeRoleUserKey);
		if (consumeRoleUser == null) {
			return new ResponseRestEntity<ConsumerRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}

		consumerRoleUserService.deleteByPrimaryKey(consumeRoleUserKey);
		return new ResponseRestEntity<ConsumerRoleUserKey>(HttpRestStatus.NO_CONTENT);
	}

}
