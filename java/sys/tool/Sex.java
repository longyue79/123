package sys.tool;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum Sex {
    MALE("男"),
    FEMALE("女");


    @EnumValue
    private final String sex;


    Sex(String sex){
        this.sex = sex;
    }

    public String getSex(){
        return sex;
    }

    public static Sex fromSex(String sex){
        for(Sex s : Sex.values()){
            if(s.sex.equals(sex)){
                return s;
            }
        }
        return null;
    }
}
