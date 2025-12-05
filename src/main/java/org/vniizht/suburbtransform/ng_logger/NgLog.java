package org.vniizht.suburbtransform.ng_logger;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Alexander Ilyin
 * <p>
 * Лог для записи в таблицу nglog.log
 */
@Builder
@Getter
public class NgLog {
    private final String messageCode;
    private final String messageText;
    private final String processName;
}
