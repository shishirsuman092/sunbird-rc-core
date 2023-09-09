package dev.sunbirdrc.registry.digilocker.pulldoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocDetailsType", propOrder = {
        "uri",
        "digiLockerId",
        "fullName",
        "dob",
        "email",
        "finalYearRollNo"
})
public class DocDetailsType {

    protected String uri;
    protected String fullName;
    protected String dob;
    protected String digiLockerId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFinalYearRollNo() {
        return finalYearRollNo;
    }

    public void setFinalYearRollNo(String finalYearRollNo) {
        this.finalYearRollNo = finalYearRollNo;
    }

    protected String email;
    protected String finalYearRollNo;
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String value) {
        this.uri = value;
    }

    public String getDigiLockerId() {
        return digiLockerId;
    }

    public void setDigiLockerId(String value) {
        this.digiLockerId = value;
    }
}

