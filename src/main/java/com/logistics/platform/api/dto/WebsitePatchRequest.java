package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class WebsitePatchRequest {

    @Size(min = 2, max = 100, message = "Code must be between 2 and 100 characters")
    private String code;

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
    private String name;

    private Platform platform;

    private StoreStatus status;

    public WebsitePatchRequest() {}

    public WebsitePatchRequest(String code, String name, Platform platform, StoreStatus status) {
        this.code = code;
        this.name = name;
        this.platform = platform;
        this.status = status;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public StoreStatus getStatus() { return status; }
    public void setStatus(StoreStatus status) { this.status = status; }
}
