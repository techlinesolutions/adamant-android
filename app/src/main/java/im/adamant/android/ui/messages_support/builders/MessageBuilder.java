package im.adamant.android.ui.messages_support.builders;

import java.sql.Timestamp;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.entities.messages.AbstractMessage;

public interface MessageBuilder<T extends AbstractMessage> {
    T build(Transaction transaction, String decryptedMessage, boolean isISayed, long date, String companionId);
}
