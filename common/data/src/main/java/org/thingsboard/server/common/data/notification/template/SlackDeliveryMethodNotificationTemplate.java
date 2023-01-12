/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.notification.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SlackDeliveryMethodNotificationTemplate extends DeliveryMethodNotificationTemplate {

    private SlackConversation.Type conversationType;
    @NotEmpty
    private String conversationId;

    public SlackDeliveryMethodNotificationTemplate(SlackDeliveryMethodNotificationTemplate other) {
        super(other);
        this.conversationType = other.conversationType;
        this.conversationId = other.conversationId;
    }

    @Override
    public NotificationDeliveryMethod getMethod() {
        return NotificationDeliveryMethod.SLACK;
    }

    @Override
    public SlackDeliveryMethodNotificationTemplate copy() {
        return new SlackDeliveryMethodNotificationTemplate(this);
    }

}
