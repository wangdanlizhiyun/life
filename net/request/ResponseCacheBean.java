package com.esell.yixinfa.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("responseCache")
public class ResponseCacheBean {
    @PrimaryKey(AssignType.BY_MYSELF)
    String key;
    public String content;
    public long saveTime;

    public ResponseCacheBean(String key, String content, long saveTime) {
        this.key = key;
        this.content = content;
        this.saveTime = saveTime;
    }

}
