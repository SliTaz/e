package com.zbensoft.e.payment.api.alarm.type.snmp;

import com.zbensoft.e.payment.api.alarm.AlarmMessage;
import com.zbensoft.e.payment.api.alarm.type.AlarmTypeProcess;

public class SnmpProcess implements AlarmTypeProcess{

	@Override
	public void process(AlarmMessage message) {
//		String SNMP_OID_HEADER=SystemConfigFactory.getInstance().getSystemConfigStr(ConfigSNMP.SNMP_OID_HEADER, "1.3.6.1.6.3.1.1.6.100.");
//		PDU pdu=CommonFun.getPDU();
//		pdu.add(new VariableBinding(new OID(SNMP_OID_HEADER+"1.1.1"), new OctetString(message.getKey())));
//		pdu.add(new VariableBinding(new OID(SNMP_OID_HEADER+"1.1.2"), new OctetString(message.getLevel())));
//		pdu.add(new VariableBinding(new OID(SNMP_OID_HEADER+"1.1.3"), new OctetString(message.getContent())));
//		pdu.add(new VariableBinding(new OID(SNMP_OID_HEADER+"1.1.4"), new OctetString(ComFunc.TodayTimeString())));
//		SNMPClient.send(pdu);
	}

}
