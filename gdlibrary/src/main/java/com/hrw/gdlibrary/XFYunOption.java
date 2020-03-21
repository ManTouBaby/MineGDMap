package com.hrw.gdlibrary;

/**
 * @author:MtBaby
 * @date:2020/03/21 16:36
 * @desc:
 */
public class XFYunOption {
    private String SPEED = "50";//设置合成语速
    private String  PITCH= "50";//设置合成音调
    private String  VOLUME= "50";//设置合成音量
    private String  STREAM_TYPE= "3";//设置播放器音频流类型

    public String getSPEED() {
        return SPEED;
    }

    public void setSPEED(String SPEED) {
        this.SPEED = SPEED;
    }

    public String getPITCH() {
        return PITCH;
    }

    public void setPITCH(String PITCH) {
        this.PITCH = PITCH;
    }

    public String getVOLUME() {
        return VOLUME;
    }

    public void setVOLUME(String VOLUME) {
        this.VOLUME = VOLUME;
    }

    public String getSTREAM_TYPE() {
        return STREAM_TYPE;
    }

    public void setSTREAM_TYPE(String STREAM_TYPE) {
        this.STREAM_TYPE = STREAM_TYPE;
    }
}
