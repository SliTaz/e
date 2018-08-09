package com.zbensoft.e.payment.api.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIGetClapResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRequest;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserClapMapper;

@Service
public class ConsumerUserClapServiceImpl implements ConsumerUserClapService {

	private static final Logger log = LoggerFactory.getLogger(ConsumerUserClapServiceImpl.class);

	@Autowired
	ConsumerUserClapMapper consumerUserClapMapper;

	@Autowired
	ConsumerFamilyMapper consumerFamilyMapper;

	@Override
	public int deleteByPrimaryKey(String consumerUserClapId) {
		return consumerUserClapMapper.deleteByPrimaryKey(consumerUserClapId);
	}

	@Override
	public int insert(ConsumerUserClap record) {
		return consumerUserClapMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerUserClap record) {
		return consumerUserClapMapper.insertSelective(record);
	}

	@Override
	public ConsumerUserClap selectByPrimaryKey(String consumerUserClapId) {
		return consumerUserClapMapper.selectByPrimaryKey(consumerUserClapId);
	}

	@Override
	public ConsumerUserClap selectByUser(String userId) {
		return consumerUserClapMapper.selectByUser(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerUserClap record) {
		return consumerUserClapMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerUserClap record) {
		return consumerUserClapMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUserClap> selectPage(ConsumerUserClap record) {
		return consumerUserClapMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumerUserClapMapper.deleteAll();
	}

	@Override
	public int count(ConsumerUserClap consumerUserClap) {
		return consumerUserClapMapper.count(consumerUserClap);
	}

	@Override
	public List<ConsumerUserClap> selectByUserId(String userId) {
		return consumerUserClapMapper.selectByUserId(userId);
	}

	@Override
	public ConsumerUserClap selectByClapId(String username) {
		return consumerUserClapMapper.selectByClapId(username);
	}

	@Override
	public ConsumerUserClap selectByIdNumber(String recvIdNumber) {
		return consumerUserClapMapper.selectByIdNumber(recvIdNumber);
	}

	@Override
	public ConsumerUserClap selectByFamilyId(String FamilyId) {
		return consumerUserClapMapper.selectByFamilyId(FamilyId);
	}

	@Override
	public List<ConsumerUserClap> selectByClapStoreNo(String clapStoreNo) {
		return consumerUserClapMapper.selectByClapStoreNo(clapStoreNo);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public ResponseRestEntity<ConsumerUserClap> synchronizeConsumerClap(ConsumerUserClap consumerUserClap, String userId, CloseableHttpClient httpClient, RequestConfig config, String URL_CLAP_GET_CLAP) {
		HttpPost httpPost = new HttpPost(URL_CLAP_GET_CLAP);
		httpPost.setConfig(config);
		APIRequest apiRequest = new APIRequest();
		String[] params = { consumerUserClap.getIdNumber() };
		apiRequest.setParams(params);
		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(apiRequest);
		StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");
		httpPost.setEntity(stringEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					if (responseStr != null && responseStr.length() > 0) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						APIGetClapResponse apiGetClapResponse = null;
						if (responseStr.contains("body")) {
							apiGetClapResponse = JSONObject.toJavaObject(jsonObjectResponse.getJSONObject("body"), APIGetClapResponse.class);
						} else {
							apiGetClapResponse = JSONObject.toJavaObject(jsonObjectResponse, APIGetClapResponse.class);
						}
						if (apiGetClapResponse != null) {
							if ("1000".equals(apiGetClapResponse.getCod())) {

								// ConsumerUserClap updateConsumerUserClap = new ConsumerUserClap();
								consumerUserClap.setConsumerUserClapId(consumerUserClap.getConsumerUserClapId());
								consumerUserClap.setCommunityCode(apiGetClapResponse.getR_codcom());
								consumerUserClap.setCommunityName(apiGetClapResponse.getR_ncom());
								consumerUserClap.setClapStoreNo(apiGetClapResponse.getR_codclap());
								consumerUserClap.setClapStoreName(apiGetClapResponse.getR_nclap());
								consumerUserClap.setFamilyId("" + apiGetClapResponse.getR_codfam());
								consumerUserClap.setBindTime(new Date());

								consumerUserClapMapper.updateByPrimaryKey(consumerUserClap);
								if (consumerUserClap.getFamilyId() != null && consumerUserClap.getFamilyId().length() > 0) {
									ConsumerFamily consumerFamily = consumerFamilyMapper.selectByPrimaryKey(consumerUserClap.getFamilyId());
									if (consumerFamily == null) {
										consumerFamily = new ConsumerFamily();
										consumerFamily.setFamilyId(consumerUserClap.getFamilyId());
										consumerFamily.setName(consumerUserClap.getFamilyId());
										consumerFamilyMapper.insert(consumerFamily);
									}
								}
								return new ResponseRestEntity<ConsumerUserClap>(consumerUserClap, HttpRestStatus.OK);
							} else {
								log.warn(responseStr);
								return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_RESPONSE_STATES_NOT_SUCC, "get patriot status not true");
							}
						} else {
							log.warn(responseStr);
							return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_RESPONSE_FORMAT_ERROR, "get patriot body object is null");
						}
					} else {
						log.warn(responseStr);
						return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_RESPONSE_BODY_NOT_EXIST, "get patriot body is null");
					}
				} else {
					return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_RESPONSE_IS_NULL, "get patriot response is null");
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.error("get patriot exception", e);
			return new ResponseRestEntity<>(HttpRestStatus.CLAP_API_FAILD, "get patriot exception");
		} finally {
		}
	}

}
