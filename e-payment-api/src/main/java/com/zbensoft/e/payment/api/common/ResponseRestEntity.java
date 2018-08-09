package com.zbensoft.e.payment.api.common;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

public class ResponseRestEntity<T> {

	private T body;
	private HttpRestStatus statusCode;
	private String statusMessage;
	private HttpHeaders headers;
	private int recordsTotal = 0;
	private int recordsFiltered = 0;

	public ResponseRestEntity(T body, HttpRestStatus httpRestStatus, String statusMessage) {
		this.body = body;
		this.statusCode = httpRestStatus;
		this.statusMessage = statusMessage;
	}

	public ResponseRestEntity(T body, HttpRestStatus httpRestStatus) {
		this(body, httpRestStatus, null);
	}

	public ResponseRestEntity(T body, HttpRestStatus httpRestStatus, String statusMessage, int recordsTotal,
			int recordsFiltered) {
		this.body = body;
		this.statusCode = httpRestStatus;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.statusMessage = statusMessage;

	}

	public ResponseRestEntity(T body, HttpRestStatus httpRestStatus, int recordsTotal, int recordsFiltered) {
		this(body, httpRestStatus, null, recordsTotal, recordsFiltered);
	}

	public ResponseRestEntity(HttpRestStatus httpRestStatus, String statusMessage) {
		this.statusCode = httpRestStatus;
		this.statusMessage = statusMessage;
	}

	public ResponseRestEntity(HttpRestStatus httpRestStatus) {
		this(httpRestStatus, null);
	}

	public ResponseRestEntity(MultiValueMap<String, String> headers) {
		this(null, headers);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and status code,
	 * and no body.
	 * 
	 * @param headers
	 *            the entity headers
	 * @param statusCode
	 *            the status code
	 */
	public ResponseRestEntity(MultiValueMap<String, String> headers, HttpRestStatus httpRestStatus,
			String statusMessage) {
		this(headers);
		this.statusCode = httpRestStatus;
		this.statusMessage = statusMessage;
	}

	public ResponseRestEntity(MultiValueMap<String, String> headers, HttpRestStatus httpRestStatus) {
		this(headers, httpRestStatus, null);
	}

	public ResponseRestEntity(T body, MultiValueMap<String, String> headers) {
		this.body = body;
		HttpHeaders tempHeaders = new HttpHeaders();
		if (headers != null) {
			tempHeaders.putAll(headers);
		}
		this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body, headers, and status
	 * code.
	 * 
	 * @param body
	 *            the entity body
	 * @param headers
	 *            the entity headers
	 * @param statusCode
	 *            the status code
	 */
	public ResponseRestEntity(T body, MultiValueMap<String, String> headers, HttpRestStatus httpRestStatus,
			String statusMessage) {
		this(body, headers);
		this.statusCode = httpRestStatus;
		this.statusMessage = statusMessage;
	}

	public ResponseRestEntity(T body, MultiValueMap<String, String> headers, HttpRestStatus httpRestStatus) {
		this(body, headers, httpRestStatus, null);
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 */
	public HttpRestStatus getStatusCode() {
		return this.statusCode;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}
		ResponseRestEntity<?> otherEntity = (ResponseRestEntity<?>) other;
		return (ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers)
				&& ObjectUtils.nullSafeEquals(this.body, otherEntity.body)
				&& ObjectUtils.nullSafeEquals(this.statusCode, otherEntity.statusCode));
	}

	@Override
	public int hashCode() {
		return (super.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.statusCode));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(this.statusCode.toString());
		builder.append(' ');
		builder.append(this.statusCode.getReasonPhrase());
		builder.append(',');
		T body = getBody();
		HttpHeaders headers = getHeaders();
		if (body != null) {
			builder.append(body);
			if (headers != null) {
				builder.append(',');
			}
		}
		if (headers != null) {
			builder.append(headers);
		}
		builder.append('>');
		return builder.toString();
	}

	public T getBody() {
		return body;
	}

	public HttpHeaders getHeaders() {
		return this.headers;
	}
}
