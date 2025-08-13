package com.targetcar.usuario.controller;

import com.targetcar.usuario.business.UsuarioService;
import com.targetcar.usuario.business.ViaCepService;
import com.targetcar.usuario.business.dto.EnderecoDTO;
import com.targetcar.usuario.business.dto.TelefoneDTO;
import com.targetcar.usuario.business.dto.UsuarioDTO;
import com.targetcar.usuario.infrastructure.clients.ViaCepDTO;
import com.targetcar.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Cadastra usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ViaCepService viaCepService;

    @PostMapping
    @Operation(summary = "Salvar um Usuários", description = "Salvar um Usuário")
    @ApiResponse(responseCode = "200", description = "Usuaário salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário",
            description = "Autentica um usuário com e-mail e senha. Retorna uma mensagem ou token em caso de sucesso."
    )
    @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<String> login(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.autenticarUsuario(usuarioDTO));
    }

    @GetMapping
    @Operation(
            summary = "Buscar usuário por e-mail",
            description = "Retorna os dados de um usuário com base no e-mail fornecido como parâmetro"
    )
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    @Operation(
            summary = "Deletar usuário por e-mail",
            description = "Remove o usuário correspondente ao e-mail fornecido"
    )
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(
            summary = "Atualizar dados do usuário",
            description = "Atualiza os dados do usuário autenticado. É necessário enviar o token no cabeçalho Authorization."
    )
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado – token inválido ou ausente")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<UsuarioDTO> atualizDadoUsuario(@RequestBody UsuarioDTO dto,
                                                         @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, dto));
    }

    @PutMapping("/endereco")
    @Operation(
            summary = "Atualizar endereço do usuário",
            description = "Atualiza os dados de endereço de um usuário com base no ID fornecido como parâmetro"
    )
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<EnderecoDTO> atualizaEndereco(@RequestBody EnderecoDTO dto,
                                                        @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizaEndereco(id, dto));
    }

    @PutMapping("/telefone")
    @Operation(
            summary = "Atualizar telefone do usuário",
            description = "Atualiza os dados de telefone de um usuário com base no ID fornecido como parâmetro"
    )
    @ApiResponse(responseCode = "200", description = "Telefone atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<TelefoneDTO> atualizaTelefone(@RequestBody TelefoneDTO dto,
                                                        @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizaTelefone(id, dto));
    }

    @PostMapping("/endereco")
    @Operation(
            summary = "Cadastrar endereço do usuário",
            description = "Cadastra um novo endereço para o usuário autenticado. É necessário enviar o token no cabeçalho Authorization."
    )
    @ApiResponse(responseCode = "200", description = "Endereço cadastrado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado – token inválido ou ausente")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<EnderecoDTO> cadastaEndereco(@RequestBody EnderecoDTO dto,
                                                       @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastraEndereco(token, dto));
    }

    @PostMapping("/telefone")
    @Operation(
            summary = "Cadastrar telefone do usuário",
            description = "Cadastra um novo telefone para o usuário autenticado. É necessário enviar o token no cabeçalho Authorization."
    )
    @ApiResponse(responseCode = "200", description = "Telefone cadastrado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado – token inválido ou ausente")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<TelefoneDTO> cadastraTelefone(@RequestBody TelefoneDTO dto,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastraTelefone(token, dto));
    }

    @GetMapping("/endereco/{cep}")
    @Operation(
            summary = "Buscar dados de endereço por CEP",
            description = "Consulta os dados de endereço a partir do CEP informado, utilizando o serviço ViaCEP"
    )
    @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso")
    @ApiResponse(responseCode = "400", description = "CEP inválido")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o CEP informado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ViaCepDTO> buscaDadosDeEndereco(
            @Parameter(description = "CEP no formato 00000000", required = true)
            @PathVariable("cep") String cep) {
        return ResponseEntity.ok(viaCepService.buscarDadosEndereco(cep));
    }

}
