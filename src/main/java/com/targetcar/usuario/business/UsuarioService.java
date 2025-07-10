package com.targetcar.usuario.business;

import com.targetcar.usuario.business.converter.UsuarioConverter;
import com.targetcar.usuario.business.dto.EnderecoDTO;
import com.targetcar.usuario.business.dto.TelefoneDTO;
import com.targetcar.usuario.business.dto.UsuarioDTO;
import com.targetcar.usuario.infrastructure.entity.Endereco;
import com.targetcar.usuario.infrastructure.entity.Telefone;
import com.targetcar.usuario.infrastructure.entity.Usuario;
import com.targetcar.usuario.infrastructure.exceptions.ConflictException;
import com.targetcar.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.targetcar.usuario.infrastructure.repository.EnderecoRepository;
import com.targetcar.usuario.infrastructure.repository.TelefoneRepository;
import com.targetcar.usuario.infrastructure.repository.UsuarioRepository;
import com.targetcar.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    //Fabricio Freitas
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TelefoneRepository telefoneRepository;
    private final EnderecoRepository enderecoRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste((usuarioDTO.getEmail()));
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public Usuario salvaUsuario(Usuario usuario) {
        try {
            emailExiste(usuario.getEmail());
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            return  usuarioRepository.save(usuario);
        } catch (ConflictException e){
            throw new ConflictException("Email já cadastrado: " + e.getCause());
        }
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email já cadastrado: " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado: " + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email)));
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }
        }

    public void deleteUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        //Aqui buscamos o email do usuario atraves do tokenn (tirar a obrigatoridade do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Busca os dados do usuario no banco de dados
        Usuario entity = usuarioRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Email não localizado: " + email));

        //Msclou os dados que recebemdo na requisição DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, entity);

        //Salvou os dados do usuario convertido e depois pegou o retorno e converteu para usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizaEndereco(long idEndereco, EnderecoDTO dto) {

        // Busca o endereço pelo ID
        Endereco entity = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado " + idEndereco));

        // Atualiza os dados do endereço com os dados do DTO
        Endereco enderecoAtualizado = usuarioConverter.updateEndereco(dto, entity);

        // Salva o endereço atualizado e retorna o DTO correspondente
        Endereco enderecoSalvo = enderecoRepository.save(enderecoAtualizado);

        return usuarioConverter.paraEnderecoDTO(enderecoSalvo);
    }


    public TelefoneDTO atualizaTelefone(long idTelefone, TelefoneDTO dto) {

        // Busca o telefone pelo ID
        Telefone telefoneAtualizado = telefoneRepository.findById(idTelefone)
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado " + idTelefone));

        // Atualiza os dados do telefone com os dados do DTO
        telefoneAtualizado = usuarioConverter.updateTelefone(dto, telefoneAtualizado);

        // Salva o telefone atualizado e retorna o DTO correspondente
        Telefone telefoneSalvo = telefoneRepository.save(telefoneAtualizado);

        return usuarioConverter.paraTelefoneDTO(telefoneSalvo);
    }

    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("Email não localizado: " + email));
        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("Email não localizado: " + email));
        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneDTO(telefoneEntity);
    }

}
