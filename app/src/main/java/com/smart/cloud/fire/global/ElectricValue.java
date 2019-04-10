package com.smart.cloud.fire.global;

import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 */
public class ElectricValue {

    /**
     * electricTime : 2016-11-15 11:51:42
     * electricType : 6
     * electricValue : [{"id":1,"value":"222.190002"},{"id":2,"value":""},{"id":3,"value":""}]
     */

    private String electricTime;
    private int electricType;
    /**
     * id : 1
     * value : 222.190002
     */

    private List<ElectricValueBean> electricValue;

    public String getElectricTime() {
        return electricTime;
    }

    public void setElectricTime(String electricTime) {
        this.electricTime = electricTime;
    }

    public int getElectricType() {
        return electricType;
    }

    public void setElectricType(int electricType) {
        this.electricType = electricType;
    }

    public List<ElectricValueBean> getElectricValue() {
        return electricValue;
    }

    public void setElectricValue(List<ElectricValueBean> electricValue) {
        this.electricValue = electricValue;
    }

    public class ElectricValueBean {
        private int electricType;//@@数据类型
        private int id;//@@回路编号
        private String value;//@@当前值
        private String ElectricThreshold;//@@阈值

        public int getId() {
            return id;
        }

        public String getElectricThreshold() {
            return ElectricThreshold;
        }

        public void setElectricThreshold(String electricThreshold) {
            ElectricThreshold = electricThreshold;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getElectricType() {
            return electricType;
        }

        public void setElectricType(int electricType) {
            this.electricType = electricType;
        }
    }
}
