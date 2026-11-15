package pe.edwin.wallet.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edwin.wallet.domain.MovementType;
import pe.edwin.wallet.domain.Wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@WebServlet(name = "WalletServlet", urlPatterns = "/wallet")
public final class WalletServlet extends HttpServlet {
    private static final String SESSION_WALLET = "wallet";
    private static final String VIEW = "/WEB-INF/views/wallet.jsp";
    private static final Pattern VALID_AMOUNT = Pattern.compile("\\d+(?:\\.\\d{1,2})?");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Wallet wallet = getOrCreateWallet(request.getSession());
        request.setAttribute("wallet", wallet);
        String success = request.getParameter("success");
        if ("income".equals(success)) {
            request.setAttribute("successMessage", "Recarga registrada correctamente.");
        } else if ("expense".equals(success)) {
            request.setAttribute("successMessage", "Gasto registrado correctamente.");
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String typeValue = request.getParameter("type");
        String amountValue = request.getParameter("amount");
        request.setAttribute("submittedType", typeValue);
        request.setAttribute("submittedAmount", amountValue);

        synchronized (session) {
            Wallet wallet = getOrCreateWallet(session);
            request.setAttribute("wallet", wallet);
            MovementType type = parseMovementType(typeValue);
            if (type == null) {
                showError(request, response, "Selecciona si deseas realizar una recarga o un gasto.");
                return;
            }
            if (amountValue == null || !VALID_AMOUNT.matcher(amountValue.trim()).matches()) {
                showError(request, response, "Ingresa un monto mayor que cero con hasta dos decimales.");
                return;
            }

            if (amountValue.trim().length() > 15) {
                showError(request, response, "El monto ingresado es demasiado grande.");
                return;
            }

            try {
                wallet.register(type, new BigDecimal(amountValue.trim()));
            } catch (IllegalArgumentException exception) {
                showError(request, response, exception.getMessage());
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/wallet?success="
                + (typeValue.equals("RECARGA") ? "income" : "expense"));
    }

    private static Wallet getOrCreateWallet(HttpSession session) {
        synchronized (session) {
            Object existing = session.getAttribute(SESSION_WALLET);
            if (existing instanceof Wallet wallet) {
                return wallet;
            }
            Wallet wallet = new Wallet();
            session.setAttribute(SESSION_WALLET, wallet);
            return wallet;
        }
    }

    private static MovementType parseMovementType(String value) {
        if (value == null) {
            return null;
        }
        try {
            return MovementType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
