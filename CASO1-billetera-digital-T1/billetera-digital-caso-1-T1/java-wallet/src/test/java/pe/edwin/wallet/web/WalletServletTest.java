package pe.edwin.wallet.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import pe.edwin.wallet.domain.Wallet;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class WalletServletTest {
    @Test
    void createsAndPreservesTheWalletInTheHttpSessionAcrossGetRequests() throws Exception {
        Fixture fixture = new Fixture();

        fixture.servlet.doGet(fixture.request, fixture.response);

        Wallet wallet = (Wallet) fixture.sessionValues.get("wallet");
        assertNotNull(wallet);
        assertEquals(new java.math.BigDecimal("250.00"), wallet.getBalance());
        assertSame(wallet, fixture.attributes.get("wallet"));
        assertEquals("/WEB-INF/views/wallet.jsp", fixture.forwardedTo);

        fixture.servlet.doGet(fixture.request, fixture.response);

        assertSame(wallet, fixture.sessionValues.get("wallet"));
    }

    @Test
    void successfulPostStoresTheMovementAndRedirectsUsingPostRedirectGet() throws Exception {
        Fixture fixture = new Fixture();
        fixture.sessionValues.put("wallet", new Wallet());
        fixture.parameters.put("type", "RECARGA");
        fixture.parameters.put("amount", "100.00");

        fixture.servlet.doPost(fixture.request, fixture.response);

        Wallet wallet = (Wallet) fixture.sessionValues.get("wallet");
        assertEquals(new java.math.BigDecimal("350.00"), wallet.getBalance());
        assertEquals(1, wallet.getMovements().size());
        assertEquals("/wallet?success=income", fixture.redirectLocation);
        assertNull(fixture.forwardedTo);
    }

    @Test
    void invalidPostForwardsToTheViewWithoutChangingTheSessionWallet() throws Exception {
        Fixture fixture = new Fixture();
        Wallet wallet = new Wallet();
        fixture.sessionValues.put("wallet", wallet);
        fixture.parameters.put("type", "GASTO");
        fixture.parameters.put("amount", "400");

        fixture.servlet.doPost(fixture.request, fixture.response);

        assertSame(wallet, fixture.sessionValues.get("wallet"));
        assertEquals(new java.math.BigDecimal("250.00"), wallet.getBalance());
        assertEquals(0, wallet.getMovements().size());
        assertEquals("Saldo insuficiente para registrar este gasto.", fixture.attributes.get("errorMessage"));
        assertEquals("/WEB-INF/views/wallet.jsp", fixture.forwardedTo);
        assertNull(fixture.redirectLocation);
    }

    @Test
    void invalidEmptyZeroAndNegativeInputsDoNotChangeTheSessionWallet() throws Exception {
        for (String amount : new String[]{null, "", "0", "-1", "abc"}) {
            Fixture fixture = new Fixture();
            Wallet wallet = new Wallet();
            fixture.sessionValues.put("wallet", wallet);
            fixture.parameters.put("type", "RECARGA");
            fixture.parameters.put("amount", amount);

            fixture.servlet.doPost(fixture.request, fixture.response);

            assertEquals(new java.math.BigDecimal("250.00"), wallet.getBalance(), "Saldo para monto " + amount);
            assertEquals(0, wallet.getMovements().size(), "Historial para monto " + amount);
            assertEquals("/WEB-INF/views/wallet.jsp", fixture.forwardedTo, "Vista para monto " + amount);
            assertNull(fixture.redirectLocation, "Redirección para monto " + amount);
        }
    }

    private static final class Fixture {
        private final Map<String, Object> sessionValues = new HashMap<>();
        private final Map<String, Object> attributes = new HashMap<>();
        private final Map<String, String> parameters = new HashMap<>();
        private final HttpSession session;
        private final RequestDispatcher dispatcher;
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final WalletServlet servlet = new WalletServlet();
        private String forwardedTo;
        private String redirectLocation;

        private Fixture() {
            session = proxy(HttpSession.class, (proxy, method, args) -> switch (method.getName()) {
                case "getAttribute" -> sessionValues.get(args[0]);
                case "setAttribute" -> {
                    sessionValues.put((String) args[0], args[1]);
                    yield null;
                }
                case "toString" -> "TestHttpSession";
                default -> defaultValue(method.getReturnType());
            });

            dispatcher = proxy(RequestDispatcher.class, (proxy, method, args) -> {
                if ("forward".equals(method.getName())) {
                    forwardedTo = "/WEB-INF/views/wallet.jsp";
                }
                return defaultValue(method.getReturnType());
            });

            request = proxy(HttpServletRequest.class, (proxy, method, args) -> switch (method.getName()) {
                case "getSession" -> session;
                case "getParameter" -> parameters.get(args[0]);
                case "getRequestDispatcher" -> dispatcher;
                case "setAttribute" -> {
                    attributes.put((String) args[0], args[1]);
                    yield null;
                }
                case "getAttribute" -> attributes.get(args[0]);
                case "getContextPath" -> "";
                case "toString" -> "TestHttpServletRequest";
                default -> defaultValue(method.getReturnType());
            });

            response = proxy(HttpServletResponse.class, (proxy, method, args) -> {
                if ("sendRedirect".equals(method.getName())) {
                    redirectLocation = (String) args[0];
                }
                return defaultValue(method.getReturnType());
            });
        }

        private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
            return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
        }

        private static Object defaultValue(Class<?> returnType) {
            if (!returnType.isPrimitive() || returnType == void.class) return null;
            if (returnType == boolean.class) return false;
            if (returnType == char.class) return '\0';
            if (returnType == byte.class) return (byte) 0;
            if (returnType == short.class) return (short) 0;
            if (returnType == int.class) return 0;
            if (returnType == long.class) return 0L;
            if (returnType == float.class) return 0.0f;
            return 0.0d;
        }
    }
}