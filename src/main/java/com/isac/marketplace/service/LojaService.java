package com.isac.marketplace.service;

import com.isac.marketplace.dto.LojaCadastroDTO;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.CnpjInvalidoException;
import com.isac.marketplace.exception.LojaJaCadastradaException;
import com.isac.marketplace.exception.TelefoneInvalidoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo cadastro e pelas regras de negócio das lojas
 * parceiras do marketplace comunitário do ISAC.
 *
 * Esta é a classe escolhida pelo grupo para ser implementada por completo
 * nesta entrega, servindo de referência para a estruturação dos demais
 * testes do projeto.
 */
@Service
public class LojaService {

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    /**
     * Cadastra uma nova loja no marketplace.
     * A loja é criada com status de assinatura PENDENTE, até que o
     * pagamento seja confirmado (fluxo a ser implementado futuramente).
     *
     * @param dto dados informados no formulário de cadastro
     * @return a loja persistida, com id gerado
     * @throws CampoObrigatorioException se algum campo obrigatório não for informado
     * @throws CnpjInvalidoException     se o CNPJ não tiver um formato válido
     * @throws TelefoneInvalidoException se o telefone/WhatsApp não tiver um formato válido
     * @throws LojaJaCadastradaException se já existir loja cadastrada com o mesmo CNPJ
     */
    public Loja cadastrarLoja(LojaCadastroDTO dto) {
        validarCamposObrigatorios(dto);
        validarCnpj(dto.getCnpj());
        validarTelefoneWhatsapp(dto.getTelefoneWhatsapp());
        verificarDuplicidade(dto.getCnpj());

        Loja loja = new Loja(
                dto.getNomeFantasia(),
                somenteNumeros(dto.getCnpj()),
                dto.getCategoria(),
                somenteNumeros(dto.getTelefoneWhatsapp()),
                dto.getEmail(),
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
        String cnpjLimpo = somenteNumeros(cnpj);
        if (cnpjLimpo.length() != 14) {
            throw new CnpjInvalidoException("O CNPJ deve conter 14 dígitos.");
        }
        // TODO: implementar validação completa dos dígitos verificadores do CNPJ.
        // Por ora, valida-se apenas o formato/tamanho.
    }

    private void validarTelefoneWhatsapp(String telefone) {
        String telefoneLimpo = somenteNumeros(telefone);
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            throw new TelefoneInvalidoException(
                    "Telefone/WhatsApp inválido. Informe o DDD + número (10 ou 11 dígitos).");
        }
    }

    private void verificarDuplicidade(String cnpj) {
        String cnpjLimpo = somenteNumeros(cnpj);
        if (lojaRepository.existsByCnpj(cnpjLimpo)) {
            throw new LojaJaCadastradaException("Já existe uma loja cadastrada com este CNPJ.");
        }
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String somenteNumeros(String valor) {
        return valor == null ? "" : valor.replaceAll("[^0-9]", "");
    }
}
