package com.isac.marketplace.service;

import com.isac.marketplace.dto.LojaCadastroDTO;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.CnpjInvalidoException;
import com.isac.marketplace.exception.EmailInvalidoException;
import com.isac.marketplace.exception.LojaJaCadastradaException;
import com.isac.marketplace.exception.TelefoneInvalidoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.util.CnpjValidator;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Cadastro de lojas parceiras. A loja nasce com assinatura PENDENTE e
 * so passa a ser ATIVA depois do pagamento (ver AssinaturaService).
 */
@Service
public class LojaService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public Loja cadastrarLoja(LojaCadastroDTO dto) {
        validarCamposObrigatorios(dto);
        validarCnpj(dto.getCnpj());
        validarEmail(dto.getEmail());
        validarTelefoneWhatsapp(dto.getTelefoneWhatsapp());

        String cnpj = somenteNumeros(dto.getCnpj());
        if (lojaRepository.existsByCnpj(cnpj)) {
            throw new LojaJaCadastradaException("Já existe uma loja cadastrada com este CNPJ.");
        }

        Loja loja = new Loja(
                dto.getNomeFantasia().trim(),
                cnpj,
                dto.getCategoria().trim(),
                somenteNumeros(dto.getTelefoneWhatsapp()),
                dto.getEmail().trim(),
                dto.getEndereco()
        );
        loja.setStatusAssinatura(StatusAssinatura.PENDENTE);

        return lojaRepository.save(loja);
    }

    private void validarCamposObrigatorios(LojaCadastroDTO dto) {
        if (isBlank(dto.getNomeFantasia())) {
            throw new CampoObrigatorioException("O nome fantasia da loja é obrigatório.");
        }
        if (isBlank(dto.getCnpj())) {
            throw new CampoObrigatorioException("O CNPJ é obrigatório.");
        }
        if (isBlank(dto.getCategoria())) {
            throw new CampoObrigatorioException("A categoria da loja é obrigatória.");
        }
        if (isBlank(dto.getTelefoneWhatsapp())) {
            throw new CampoObrigatorioException("O telefone/WhatsApp é obrigatório.");
        }
        if (isBlank(dto.getEmail())) {
            throw new CampoObrigatorioException("O e-mail é obrigatório.");
        }
    }

    private void validarCnpj(String cnpj) {
        if (somenteNumeros(cnpj).length() != 14) {
            throw new CnpjInvalidoException("O CNPJ deve conter 14 dígitos.");
        }
        if (!CnpjValidator.isValido(cnpj)) {
            throw new CnpjInvalidoException("CNPJ inválido (dígitos verificadores incorretos).");
        }
    }

    private void validarEmail(String email) {
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new EmailInvalidoException("O e-mail informado não é válido.");
        }
    }

    private void validarTelefoneWhatsapp(String telefone) {
        int tamanho = somenteNumeros(telefone).length();
        if (tamanho < 10 || tamanho > 11) {
            throw new TelefoneInvalidoException(
                    "Telefone/WhatsApp inválido. Informe o DDD + número (10 ou 11 dígitos).");
        }
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String somenteNumeros(String valor) {
        return valor == null ? "" : valor.replaceAll("[^0-9]", "");
    }
}
