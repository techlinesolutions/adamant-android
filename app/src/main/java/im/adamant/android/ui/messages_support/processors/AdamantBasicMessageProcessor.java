package im.adamant.android.ui.messages_support.processors;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.HashMap;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.exceptions.EncryptionException;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.exceptions.NotFoundPublicKey;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static im.adamant.android.core.AdamantApi.MINIMUM_COST;

public class AdamantBasicMessageProcessor extends AbstractMessageProcessor<AdamantBasicMessage>{

    public AdamantBasicMessageProcessor(AdamantApiWrapper api, Encryptor encryptor, PublicKeyStorage publicKeyStorage) {
        super(api, encryptor, publicKeyStorage);
    }

    @Override
    public long calculateMessageCostInAdamant(AdamantBasicMessage message) {
        int countPaymentBlocks = message.getText().length() / 256;

        if (countPaymentBlocks <= 0){
            countPaymentBlocks = 1;
        } else {
            if ((message.getText().length() % 256) != 0){
                countPaymentBlocks += 1;
            }
        }

        return countPaymentBlocks * MINIMUM_COST;
    }

    @Override
    public Single<UnnormalizedTransactionMessage> buildTransactionMessage(AdamantBasicMessage message, String recipientPublicKey) {
        if (!api.isAuthorized()){return Single.error(new NotAuthorizedException("Not authorized"));}

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        return Single
                .defer(() -> {
                    if (recipientPublicKey == null || recipientPublicKey.isEmpty()) {
                        return Single.error(new NotFoundPublicKey("Recipient public key not found"));
                    }

                    TransactionMessage transactionMessage = encryptor.encryptMessage(
                            message.getText(),
                            recipientPublicKey,
                            keyPair.getSecretKeyString().toLowerCase()
                    );

                    if (transactionMessage == null) {
                        return Single.error(new EncryptionException("Error when encrypting message"));
                    } else {
                        return Single.just(transactionMessage);
                    }
                })
                .flatMap((transactionMessage -> Single.fromCallable(
                        () -> {
                            UnnormalizedTransactionMessage unnormalizedMessage = new UnnormalizedTransactionMessage();
                            unnormalizedMessage.setMessage(transactionMessage.getMessage());
                            unnormalizedMessage.setOwnMessage(transactionMessage.getOwnMessage());
                            unnormalizedMessage.setMessageType(TransactionMessage.BASE_MESSAGE_TYPE);
                            unnormalizedMessage.setType(Transaction.CHAT_MESSAGE);
                            unnormalizedMessage.setPublicKey(keyPair.getPublicKeyString().toLowerCase());
                            unnormalizedMessage.setRecipientId(message.getCompanionId());
                            unnormalizedMessage.setSenderId(account.getAddress());

                            return unnormalizedMessage;
                        }
                )));
    }
}
