package com.athelite.Model;

public class UserDefinedField {
    private String _fieldName;
    private String _data;

    public UserDefinedField(String fieldName, String data) {
        this._fieldName = fieldName;
        this._data = data;
    }

    public String getFieldName() {
        return this._fieldName;
    }

    public String getData() {
        return this._data;
    }

    public void setFieldName(String fieldName) {
        this._fieldName = fieldName;
    }

    public void setData(String data) {
        this._data = data;
    }
}
