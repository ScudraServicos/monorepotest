package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.PaymentPermissionValidator
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.PaymentPermissionValidatorImpl
import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.accessControl.domain.AccessControlUserGroup
import br.com.ume.application.shared.accessControl.gateway.AccessControlGatewayImpl
import br.com.ume.application.shared.accessControl.utils.AccessControlUserGroupRetriever
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.*
import org.mockito.Mockito

class PaymentPermissionValidatorTests {

    private lateinit var accessControlGatewayMock: AccessControlGatewayImpl
    private lateinit var accessControlUserGroupRetrieverMock: AccessControlUserGroupRetriever
    private lateinit var paymentPermissionValidator: PaymentPermissionValidator

    @BeforeEach()
    fun setup() {
        accessControlGatewayMock = Mockito.mock(AccessControlGatewayImpl::class.java)
        accessControlUserGroupRetrieverMock = Mockito.mock(AccessControlUserGroupRetriever::class.java)
        paymentPermissionValidator = PaymentPermissionValidatorImpl(
            accessControlGatewayMock, accessControlUserGroupRetrieverMock
        )
    }

    companion object {
        private val defaultUserId = "userId"
        private val defaultBeneficiaryDocument = "21744033000116"
        private val defaultGroupName = "G1"
        private val defaultBrCode = PixApiInspectResponseBuilder.buildStatic()
        private val defaultAccessControl: AccessControl = AccessControl(
            defaultUserId, setOf(defaultGroupName), true
        )
        private val defaultAccessControlUserGroup = AccessControlUserGroup(
            defaultGroupName, false, emptySet()
        )
    }

    @Nested
    @DisplayName("Execute()")
    inner class Execute {
        @Test
        fun `Should run successfully when user does not depends on store`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId)).thenReturn(defaultAccessControl)
            Mockito.`when`(accessControlUserGroupRetrieverMock.get(defaultGroupName))
                .thenReturn(defaultAccessControlUserGroup)

            paymentPermissionValidator.execute(defaultBrCode, defaultUserId)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
            Mockito.verify(accessControlUserGroupRetrieverMock, Mockito.times(1)).get(defaultGroupName)
        }

        @Test
        fun `Should run successfully when user depends on store and is allowed to perform payment`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId)).thenReturn(defaultAccessControl)
            Mockito.`when`(accessControlUserGroupRetrieverMock.get(defaultGroupName))
                .thenReturn(AccessControlUserGroup(defaultGroupName, true, setOf(defaultBeneficiaryDocument)))

            paymentPermissionValidator.execute(defaultBrCode, defaultUserId)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
            Mockito.verify(accessControlUserGroupRetrieverMock, Mockito.times(1)).get(defaultGroupName)
        }

        @Test
        fun `Should throw PERMISSION_DENIED when user's access control is not found`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId)).thenReturn(null)

            val exception = assertThrows<BrcodeValidationException> {
                paymentPermissionValidator.execute(defaultBrCode, defaultUserId)
            }
            Assertions.assertEquals(InspectBrcodeErrorEnum.PERMISSION_DENIED.toString(), exception.message)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
        }

        @Test
        fun `Should throw PERMISSION_DENIED when user does not have access`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId))
                .thenReturn(AccessControl(defaultUserId, setOf(defaultGroupName), false))

            val exception = assertThrows<BrcodeValidationException> {
                paymentPermissionValidator.execute(defaultBrCode, defaultUserId)
            }
            Assertions.assertEquals(InspectBrcodeErrorEnum.PERMISSION_DENIED.toString(), exception.message)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
        }

        @Test
        fun `Should throw PERMISSION_DENIED when user depends on store and is not allowed to perform payment`() {
            val notAllowedStoreDocument = "31744033000115"
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId)).thenReturn(defaultAccessControl)
            Mockito.`when`(accessControlUserGroupRetrieverMock.get(defaultGroupName))
                .thenReturn(AccessControlUserGroup(defaultGroupName, true, setOf(notAllowedStoreDocument)))

            val exception = assertThrows<BrcodeValidationException> {
                paymentPermissionValidator.execute(defaultBrCode, defaultUserId)
            }
            Assertions.assertEquals(InspectBrcodeErrorEnum.PERMISSION_DENIED.toString(), exception.message)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
            Mockito.verify(accessControlUserGroupRetrieverMock, Mockito.times(1)).get(defaultGroupName)
        }

        @Test
        fun `Should fail if getAccessControl fails`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId))
                .thenThrow(RuntimeException("ERROR"))

            val exception = assertThrows<RuntimeException> {
                paymentPermissionValidator.execute(defaultBrCode, defaultUserId)
            }
            Assertions.assertEquals("ERROR", exception.message)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
        }

        @Test
        fun `Should fail if accessControlUserGroupRetriever fails`() {
            Mockito.`when`(accessControlGatewayMock.getAccessControl(defaultUserId)).thenReturn(defaultAccessControl)
            Mockito.`when`(accessControlUserGroupRetrieverMock.get(defaultGroupName))
                .thenThrow(RuntimeException("ERROR"))

            val exception = assertThrows<RuntimeException> {
                paymentPermissionValidator.execute(defaultBrCode, defaultUserId)
            }
            Assertions.assertEquals("ERROR", exception.message)

            Mockito.verify(accessControlGatewayMock, Mockito.times(1)).getAccessControl(defaultUserId)
            Mockito.verify(accessControlUserGroupRetrieverMock, Mockito.times(1)).get(defaultGroupName)
        }
    }
}