package com.example.train.generator.util;

public class Field {
    private String name;
    private String nameHump;
    private String nameBigHump;
    private String nameCn;
    private String type;
    private String javaType;
    private String comment;
    private Boolean nullAble;
    private Integer length;
    private Boolean enums;
    private String enumsConst;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnumsConst() {
        return enumsConst;
    }

    public void setEnumsConst(String enumsConst) {
        this.enumsConst = enumsConst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getEnums() {
        return enums;
    }

    public void setEnums(Boolean enums) {
        this.enums = enums;
    }

    public String getNameHump() {
        return nameHump;
    }

    public void setNameHump(String nameHump) {
        this.nameHump = nameHump;
    }

    public String getNameBigHump() {
        return nameBigHump;
    }

    public void setNameBigHump(String nameBigHump) {
        this.nameBigHump = nameBigHump;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public Boolean getNullAble() {
        return nullAble;
    }

    public void setNullAble(Boolean nullAble) {
        this.nullAble = nullAble;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Field{");
        sb.append("name='").append(name).append('\'');
        sb.append(", nameHump='").append(nameHump).append('\'');
        sb.append(", nameBigHump='").append(nameBigHump).append('\'');
        sb.append(", nameCn='").append(nameCn).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", javaType='").append(javaType).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", nullAble=").append(nullAble);
        sb.append(", length=").append(length);
        sb.append(", enums=").append(enums);
        sb.append(", enumsConst='").append(enumsConst).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
