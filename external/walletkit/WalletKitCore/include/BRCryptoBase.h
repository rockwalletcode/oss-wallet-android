//
//  BRCryptoBase.h
//  BRCore
//
//  Created by Ed Gamble on 3/19/19.
//  Copyright © 2019 breadwallet. All rights reserved.
//
//  See the LICENSE file at the project root for license information.
//  See the CONTRIBUTORS file at the project root for a list of contributors.

#ifndef BRCryptoBase_h
#define BRCryptoBase_h

#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <inttypes.h>
#include <stdatomic.h>
#include <memory.h>
#include <assert.h>

// temporary

#if !defined (OwnershipGiven)
#define OwnershipGiven
#endif

#if !defined (OwnershipKept)
#define OwnershipKept
#endif

#if !defined (Nullable)
#define Nullable
#endif

#ifdef __cplusplus
extern "C" {
#endif
    extern const char* cryptoVersion;

    // Forward Declarations - Required for BRCryptoListener
    typedef struct BRCryptoTransferRecord      *BRCryptoTransfer;
    typedef struct BRCryptoWalletRecord        *BRCryptoWallet;        // BRCrypto{Transfer,Payment}
    typedef struct BRCryptoWalletManagerRecord *BRCryptoWalletManager; // BRCrypto{Wallet,Transfer,Payment}
    typedef struct BRCryptoNetworkRecord       *BRCryptoNetwork;
    typedef struct BRCryptoSystemRecord        *BRCryptoSystem;        // BRCrypto{Manager,Wallet,Transfer,Payment}
    typedef struct BRCryptoListenerRecord      *BRCryptoListener;
    typedef void  *BRCryptoListenerContext;

    // Cookies are used as markers to match up an asynchronous operation
    // request with its corresponding event.
    typedef void *BRCryptoCookie;

    typedef enum {
        CRYPTO_FALSE = 0,
        CRYPTO_TRUE  = 1
    } BRCryptoBoolean;

#define AS_CRYPTO_BOOLEAN(zeroIfFalse)   ((zeroIfFalse) ? CRYPTO_TRUE : CRYPTO_FALSE)

    // For use in Swift/Java
    typedef size_t BRCryptoCount;

    // For use in Swift/Java
    static inline void
    cryptoMemoryFree (void *memory) {
        if (NULL != memory) free (memory);
    }

    // For use in Java (needing an 'extern' for a 'native' declaration)
    extern void cryptoMemoryFreeExtern (void *memory);

    // Same as: BRBlockHeight
    typedef uint64_t BRCryptoBlockNumber;
#if !defined(BLOCK_HEIGHT_UNBOUND)
// See BRBase.h
#define BLOCK_HEIGHT_UNBOUND       (UINT64_MAX)
#endif
extern uint64_t BLOCK_HEIGHT_UNBOUND_VALUE;

#define BLOCK_NUMBER_UNKNOWN        (BLOCK_HEIGHT_UNBOUND)

    /// The number of seconds since the Unix epoch).
    typedef uint64_t BRCryptoTimestamp;

#define AS_CRYPTO_TIMESTAMP(unixSeconds)      ((BRCryptoTimestamp) (unixSeconds))
#define NO_CRYPTO_TIMESTAMP                   (AS_CRYPTO_TIMESTAMP (0))


    /// MARK: - Data32 / Data16

    typedef struct {
        uint8_t data[256/8];
    } BRCryptoData32;

    static inline void cryptoData32Clear (BRCryptoData32 *data32) {
        memset (data32, 0, sizeof (BRCryptoData32));
    }

    typedef struct {
        uint8_t data[128/8];
    } BRCryptoData16;

    static inline void cryptoData16Clear (BRCryptoData16 *data16) {
        memset (data16, 0, sizeof (BRCryptoData16));
    }

    /// MARK: - Variable Size Data

    typedef struct {
        uint8_t * bytes;
        size_t size;
    } BRCryptoData;

    static inline BRCryptoData cryptoDataNew (size_t size) {
        BRCryptoData data;
        data.size = size;
        if (size < 1) data.size = 1;
        data.bytes = calloc (data.size, sizeof(uint8_t));
        assert (data.bytes != NULL);
        return data;
    }

    static inline BRCryptoData cryptoDataCopy (uint8_t * bytes, size_t size) {
        BRCryptoData data;
        data.bytes = malloc (size * sizeof(uint8_t));
        memcpy (data.bytes, bytes, size);
        data.size = size;
        return data;
    }

    static inline BRCryptoData
    cryptoDataConcat (BRCryptoData * fields, size_t numFields) {
        size_t totalSize = 0;
        for (int i=0; i < numFields; i++) {
            totalSize += fields[i].size;
        }
        BRCryptoData concat = cryptoDataNew (totalSize);
        totalSize = 0;
        for (int i=0; i < numFields; i++) {
            memcpy (&concat.bytes[totalSize], fields[i].bytes, fields[i].size);
            totalSize += fields[i].size;
        }
        return concat;
    }

    static inline void cryptoDataFree (BRCryptoData data) {
        if (data.bytes) free(data.bytes);
        data.bytes = NULL;
        data.size = 0;
    }

    /// MARK: Network Canonical Type

    ///
    /// Crypto Network Type
    ///
    /// Try as we might, there are certain circumstances where the type of the network needs to
    /// be known.  Without this enumeration, one uses hack-arounds like:
    ///    "btc" == network.currency.code
    /// So, provide these and expect them to grow.
    ///
    /// Enumerations here need to be consistent with the networks defined in;
    ///    crypto/BRCryptoConfig.h
    ///
    typedef enum {
        CRYPTO_NETWORK_TYPE_BTC,
        CRYPTO_NETWORK_TYPE_BCH,
        CRYPTO_NETWORK_TYPE_BSV,
        CRYPTO_NETWORK_TYPE_ETH,
        CRYPTO_NETWORK_TYPE_XRP,
        CRYPTO_NETWORK_TYPE_HBAR,
        CRYPTO_NETWORK_TYPE_XTZ,
        // CRYPTO_NETWORK_TYPE_XLM,
    } BRCryptoBlockChainType;

#    define NUMBER_OF_NETWORK_TYPES     (1 + CRYPTO_NETWORK_TYPE_XTZ)
#    define CRYPTO_NETWORK_TYPE_UNKNOWN (UINT32_MAX)
    //
    // Crypto Network Base Currency
    //
    // These are the 'currency codes' used for DEFINE_CURRENCY in crypto/BRCryptoConfig.h.  Any
    // time we need 'type -> string' we'll use these in cryptoNetworkCanonicalTypeGetCurrencyCode()
    //
#    define CRYPTO_NETWORK_CURRENCY_BTC     "btc"
#    define CRYPTO_NETWORK_CURRENCY_BCH     "bch"
#    define CRYPTO_NETWORK_CURRENCY_BSV     "bsv"
#    define CRYPTO_NETWORK_CURRENCY_ETH     "eth"
#    define CRYPTO_NETWORK_CURRENCY_XRP     "xrp"
#    define CRYPTO_NETWORK_CURRENCY_HBAR    "hbar"
#    define CRYPTO_NETWORK_CURRENCY_XTZ     "xtz"

    extern const char *
    cryptoBlockChainTypeGetCurrencyCode (BRCryptoBlockChainType type);

    // MARK: - Status

    typedef enum {
        CRYPTO_SUCCESS = 0,
        // Generic catch-all failure. This should only be used as if creating a
        // specific error code does not make sense (you really should create
        // a specifc error code...).
        CRYPTO_ERROR_FAILED,

        // Reference access
        CRYPTO_ERROR_UNKNOWN_NODE = 10000,
        CRYPTO_ERROR_UNKNOWN_TRANSFER,
        CRYPTO_ERROR_UNKNOWN_ACCOUNT,
        CRYPTO_ERROR_UNKNOWN_WALLET,
        CRYPTO_ERROR_UNKNOWN_BLOCK,
        CRYPTO_ERROR_UNKNOWN_LISTENER,

        // Node
        CRYPTO_ERROR_NODE_NOT_CONNECTED = 20000,

        // Transfer
        CRYPTO_ERROR_TRANSFER_HASH_MISMATCH = 30000,
        CRYPTO_ERROR_TRANSFER_SUBMISSION,

        // Numeric
        CRYPTO_ERROR_NUMERIC_PARSE = 40000,

        // Acount
        // Wallet
        // Block
        // Listener
    } BRCryptoStatus;

    /// MARK: - Reference Counting

    typedef struct {
        _Atomic(unsigned int) count;
        void (*free) (void *);
    } BRCryptoRef;

#if defined (CRYPTO_REF_DEBUG)
#include <stdio.h>
static int cryptoRefDebug = 1;
#define cryptoRefShow   printf
#else
static int cryptoRefDebug = 0;
#define cryptoRefShow 
#endif

#define DECLARE_CRYPTO_GIVE_TAKE(type, preface)                                   \
  extern type preface##Take (type obj);                                           \
  extern type preface##TakeWeak (type obj);                                       \
  extern void preface##Give (type obj)

#define IMPLEMENT_CRYPTO_GIVE_TAKE(type, preface)                                 \
  static void preface##Release (type obj);                                        \
  extern type                                                                     \
  preface##Take (type obj) {                                                      \
    if (NULL == obj) return NULL;                                                 \
    unsigned int _c = atomic_fetch_add (&obj->ref.count, 1);                      \
    /* catch take after release */                                                \
    assert (0 != _c);                                                             \
    return obj;                                                                   \
  }                                                                               \
  extern type                                                                     \
  preface##TakeWeak (type obj) {                                                  \
    if (NULL == obj) return NULL;                                                 \
    unsigned int _c = atomic_load(&obj->ref.count);                               \
    /* keep trying to take unless object is released */                           \
    while (_c != 0 &&                                                             \
           !atomic_compare_exchange_weak (&obj->ref.count, &_c, _c + 1)) {}       \
    if (cryptoRefDebug && 0 == _c) { cryptoRefShow ("CRY: Missed: %s\n", #type); }\
    return (_c != 0) ? obj : NULL;                                                \
  }                                                                               \
  extern void                                                                     \
  preface##Give (type obj) {                                                      \
    if (NULL == obj) return;                                                      \
    unsigned int _c = atomic_fetch_sub (&obj->ref.count, 1);                      \
    /* catch give after release */                                                \
    assert (0 != _c);                                                             \
    if (1 == _c) {                                                                \
        if (cryptoRefDebug) { cryptoRefShow ("CRY: Release: %s\n", #type); }      \
        obj->ref.free (obj);                                                      \
    }                                                                             \
  }

#define CRYPTO_AS_FREE(release)     ((void (*) (void *)) release)

#define CRYPTO_REF_ASSIGN(release)  (BRCryptoRef) { 1, CRYPTO_AS_FREE (release) }

#if !defined (private_extern)
#  define private_extern          extern
#endif

#ifdef __cplusplus
}
#endif

#endif /* BRCryptoBase_h */
