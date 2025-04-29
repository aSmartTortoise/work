package com.voice.sdk.device.carservice.vcar;

import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public class CarPropertyValue<T> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final int MGR_ID_BIT_MASK = -16777216;
    public static final int MGR_ID_BIT_OFFSET = 24;
    private final int mPropertyId;
    private final int mAreaId;
    private final int mStatus;
    private final long mTimestamp;
    private final T mValue;
    private boolean mRelative;
    private Object mExtension;
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_UNAVAILABLE = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_NO_VALID = 3;

    public CarPropertyValue(int propertyId, T value) {
        this(propertyId, 0, 0, SystemClock.uptimeMillis(), value);
    }

    public CarPropertyValue(int propertyId, int areaId, T value) {
        this(propertyId, areaId, 0, SystemClock.uptimeMillis(), value);
    }

    public CarPropertyValue(int propertyId, int areaId, int status, long timestamp, T value) {
        this.mRelative = false;
        this.mPropertyId = propertyId;
        this.mAreaId = areaId;
        this.mStatus = status;
        this.mTimestamp = timestamp;
        this.mValue = value;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CarPropertyValue)) {
            return false;
        } else {
            CarPropertyValue<?> value = (CarPropertyValue)o;
            return this.mPropertyId == value.mPropertyId && this.mAreaId == value.mAreaId && this.mStatus == value.mStatus && this.mRelative == value.mRelative && deepEquals(this.mValue, value.mValue) && Objects.equals(this.mExtension, value.mExtension);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mPropertyId, this.mAreaId, this.mStatus, this.mValue, this.mRelative, this.mExtension});
    }

    public static final int decodeMgrId(int propid) {
        return propid >> 24;
    }

    public int describeContents() {
        return 0;
    }


    public int getPropertyId() {
        return this.mPropertyId;
    }

    public int getAreaId() {
        return this.mAreaId;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    @Nullable
    public T getValue() {
        return this.mValue;
    }

    public void setRelative(boolean relative) {
        this.mRelative = relative;
    }

    public boolean getRelative() {
        return this.mRelative;
    }

    public void setExtension(Object extension) {
        this.mExtension = extension;
    }

    public Object getExtension() {
        return this.mExtension;
    }

    public String toString() {
        return "CarPropertyValue{id=0x" + Integer.toHexString(this.mPropertyId) + ", area=0x" + Integer.toHexString(this.mAreaId) + ", status=" + this.mStatus + ", value=" + stringOf(this.mValue) + ", time=" + this.mTimestamp + ", relative=" + this.mRelative + ", ext=" + stringOf(this.mExtension) + '}';
    }

    static boolean deepEquals(Object e1, Object e2) {
        if (e1 == e2) {
            return true;
        } else if (e1 != null && e1.equals(e2)) {
            return true;
        } else if (e1 != null && e2 != null) {
            Class<?> cl1 = e1.getClass().getComponentType();
            Class<?> cl2 = e2.getClass().getComponentType();
            if (cl1 == Byte.class) {
                return Arrays.equals((Byte[])e1, (Byte[])e2);
            } else if (cl1 == Short.class) {
                return Arrays.equals((Short[])e1, (Short[])e2);
            } else if (cl1 == Integer.class) {
                return Arrays.equals((Integer[])e1, (Integer[])e2);
            } else if (cl1 == Long.class) {
                return Arrays.equals((Long[])e1, (Long[])e2);
            } else if (cl1 == Charset.class) {
                return Arrays.equals((Charset[])e1, (Charset[])e2);
            } else if (cl1 == Float.class) {
                return Arrays.equals((Float[])e1, (Float[])e2);
            } else if (cl1 == Double.class) {
                return Arrays.equals((Double[])e1, (Double[])e2);
            } else {
                return cl1 == Boolean.class ? Arrays.equals((Boolean[])e1, (Boolean[])e2) : Objects.deepEquals(e1, e2);
            }
        } else {
            return false;
        }
    }

    static String stringOf(Object o) {
        if (o == null) {
            return "null";
        } else {
            Class<?> clo = o.getClass().getComponentType();
            if (o instanceof Object[]) {
                return Arrays.toString((Object[])o);
            } else if (clo == Byte.class) {
                return Arrays.toString((Byte[])o);
            } else if (clo == Short.class) {
                return Arrays.toString((Short[])o);
            } else if (clo == Integer.class) {
                return Arrays.toString((Integer[])o);
            } else if (clo == Long.class) {
                return Arrays.toString((Long[])o);
            } else if (clo == Charset.class) {
                return Arrays.toString((Charset[])o);
            } else if (clo == Float.class) {
                return Arrays.toString((Float[])o);
            } else if (clo == Double.class) {
                return Arrays.toString((Double[])o);
            } else if (clo == Boolean.class) {
                return Arrays.toString((Boolean[])o);
            } else if (clo == Byte.TYPE) {
                return Arrays.toString((byte[])o);
            } else if (clo == Short.TYPE) {
                return Arrays.toString((short[])o);
            } else if (clo == Integer.TYPE) {
                return Arrays.toString((int[])o);
            } else if (clo == Long.TYPE) {
                return Arrays.toString((long[])o);
            } else if (clo == Character.TYPE) {
                return Arrays.toString((char[])o);
            } else if (clo == Float.TYPE) {
                return Arrays.toString((float[])o);
            } else if (clo == Double.TYPE) {
                return Arrays.toString((double[])o);
            } else {
                return clo == Boolean.TYPE ? Arrays.toString((boolean[])o) : o.toString();
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PropertyStatus {
    }
}
