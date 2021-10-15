package com.terraboxstudios.instantreplay.versionspecific.v1_15_R1.util;

import com.terraboxstudios.instantreplay.versionspecific.util.UtilsHelper;
import net.md_5.bungee.api.chat.ClickEvent;

public class UtilsHelperImpl implements UtilsHelper {

    @Override
    public ClickEvent getTimestampMessageClickEvent(long timestamp) {
        return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(timestamp));
    }

}
