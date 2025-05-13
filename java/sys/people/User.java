package sys.people;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import sys.tool.Email;
import sys.tool.Judgment;
import sys.tool.PhoneNumber;
import sys.tool.Sex;

@TableName("em_information")
public class User {
    @ExcelProperty("员工ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ExcelProperty("名字")
    private String name;
    @ExcelProperty("性别")
    private Sex sex;
    @ExcelProperty("电话号码")
    private PhoneNumber phone_number;
    @ExcelProperty("邮箱地址")
    private Email mail;
    @ExcelProperty("面部信息")
    private String code;

    public User(Integer id, String name, Sex sex, String phone1, String mail1, String code) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        phone_number = new PhoneNumber(phone1, new Judgment().isChinaPhone(phone1));
        mail = new Email(mail1);
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone_number.getNumber();
    }

    public void setPhone(String phone2) {
        phone_number = new PhoneNumber(phone2, new Judgment().isChinaPhone(phone2));
    }

    public String getMail() {
        return mail.getAddress();
    }

    public void setMail(String mail2) {
        mail = new Email(mail2);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if(other.id != null)
                return false;
        }else if(!id.equals(other.id))
            return false;
        return true;
    }
}
