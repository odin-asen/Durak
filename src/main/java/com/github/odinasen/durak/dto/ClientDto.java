package com.github.odinasen.durak.dto;

/**
 * DTO fuer einen Client.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ClientDto {
  public int id;
  public String name;

  public ClientDto(int id, String name) {
    this.id = id;
    this.name = name;
  }


  /**
   * Setzt das {@link #id}-Objekt.
   *
   * @param id das {@link #id}-Objekt
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * @return das {@link #id}-Objekt
   */
  public int getId() {
    return id;
  }

  /**
   * Setzt das {@link #name}-Objekt.
   *
   * @param name das {@link #name}-Objekt
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return das {@link #name}-Objekt
   */
  public String getName() {
    return name;
  }
}
